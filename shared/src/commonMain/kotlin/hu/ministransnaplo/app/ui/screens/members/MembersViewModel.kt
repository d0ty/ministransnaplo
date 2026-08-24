/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.models.Member
import hu.ministransnaplo.app.ui.components.DCFieldValue
import hu.ministransnaplo.app.util.DbResult
import hu.ministransnaplo.app.util.executeSupabaseAction
import hu.ministransnaplo.app.util.invokeWithJsonBody
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.put

class MembersViewModel : AppViewModel() {
    data class MemberTableOrder(
        val column: MemberTableColumns? = null,
        val ascending: Boolean = true,
    )

    data class MemberTableState(
        val isLoading: Boolean = false,
        val query: String = "",
        val order: MemberTableOrder = MemberTableOrder(),
        val members: List<Member> = emptyList(),
    )

    val tableState: StateFlow<MemberTableState>
        field = MutableStateFlow(MemberTableState())

    val currentMember: StateFlow<Member?>
        field = MutableStateFlow(null)

    init {
        fetchMemberTable()
    }

    fun selectMember(member: Member) {
        println("Selected member: ${Json.encodeToString(member)}")
        currentMember.update { member }
    }

    fun fetchMemberTable(query: String = "", orderColumn: MemberTableColumns? = null, ascending: Boolean = true) {
        viewModelScope.launch {
            tableState.update { it.copy(isLoading = true, order = MemberTableOrder(orderColumn, ascending)) }
            supabase.auth.awaitInitialization()
            supabase.from("member_data").select() {
                if (orderColumn != null && orderColumn.id != null)
                    order(
                        orderColumn.id, if (ascending) Order.ASCENDING else Order.DESCENDING,
                        orderColumn == MemberTableColumns.Rank && ascending
                    )
                if (query.isNotBlank()) filter {
                    like("name", "%$query%")
                }
            }.decodeAs<List<Member>>().also { result ->
                tableState.update { MemberTableState(false, query, MemberTableOrder(orderColumn, ascending), result) }
            }
        }
    }

    private fun fetchMemberTable() {
        fetchMemberTable(
            tableState.value.query,
            tableState.value.order.column,
            tableState.value.order.ascending
        )
    }

    private suspend fun inviteLeaderInternal(member: Member, email: String): DbResult {
        supabase.functions.invokeWithJsonBody("invite-user") {
            put("member_id", member.id)
            put("email", email)
        }
        return DbResult.Success.NoContent
    }

    fun createMember(name: String, isLecturer: Boolean, email: String, onResult: (DbResult) -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            executeSupabaseAction {
                val member = supabase.from("member")
                    .insert(Member.New(guard = userState.value.guard!!.id, name = name, isLecturer = isLecturer)) {
                        select()
                    }.decodeSingle<Member>()
                if (email.isBlank()) {
                    return@executeSupabaseAction DbResult.Success.WithContent(member)
                }
                inviteLeaderInternal(member, email)
                return@executeSupabaseAction DbResult.Success.WithContent(member)
            }.also(onResult)
        }
    }

    fun updateMember(data: Map<String, DCFieldValue>, onResult: (DbResult) -> Unit) {
        if (currentMember.value == null) throw IllegalStateException("Can't update no member")
        viewModelScope.launch(Dispatchers.Default) {
            executeSupabaseAction {
                supabase.from("member").update(data) {
                    select()
                    filter {
                        eq("id", currentMember.value!!.id)
                    }
                }.decodeSingle<Member>()
                    .also { updated -> currentMember.update { updated.copy(email = it?.email) } }
                fetchMemberTable()
                return@executeSupabaseAction DbResult.Success.NoContent
            }.also(onResult)
        }
    }

    fun inviteLeader(email: String, onResult: suspend (DbResult) -> Unit) {
        if (currentMember.value == null) throw IllegalStateException("Can't invite leader without a current member selected")
        viewModelScope.launch(Dispatchers.Default) {
            val result = executeSupabaseAction {
                inviteLeaderInternal(currentMember.value!!, email)
            }
            fetchMemberTable()
            onResult(result)
        }
    }

    fun deleteMember(onResult: suspend (DbResult) -> Unit) {
        if (currentMember.value == null) throw IllegalStateException("Can't delete member without a current member selected")
        viewModelScope.launch(Dispatchers.Default) {
            val result = executeSupabaseAction {
                if (currentMember.value!!.isLeader) {
                    val userDeleteResp = supabase.functions.invokeWithJsonBody("delete-user") {
                        put("member_login", currentMember.value!!.userId)
                    }
                    if (!userDeleteResp.status.isSuccess()) {
                        return@executeSupabaseAction DbResult.Failure.Error
                    }
                }
                supabase.from("member").delete {
                    filter {
                        eq("id", currentMember.value!!.id)
                    }
                }
                val result = DbResult.Success.WithContent(currentMember.value!!)
                currentMember.update { null }
                fetchMemberTable()
                result
            }
            onResult(result)
        }
    }

    fun resetLeaderMFA(onResult: suspend (DbResult) -> Unit) {
        if (currentMember.value == null) throw IllegalStateException("Can't reset no leader member MFA")
        if (currentMember.value!!.isLeader.not()) throw IllegalStateException("Can only reset leader member MFA")
        viewModelScope.launch(Dispatchers.Default) {
            val result = executeSupabaseAction {
                supabase.functions.invokeWithJsonBody("reset-mfa") {
                    put("user_id", currentMember.value!!.userId)
                }.status.isSuccess().let {
                    if (it) DbResult.Success.NoContent else DbResult.Failure.Error
                }
            }
            onResult(result)
        }
    }
}