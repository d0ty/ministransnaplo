/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.models.Member
import hu.ministransnaplo.app.util.DbResult
import hu.ministransnaplo.app.util.invokeWithJsonBody
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    init {
        fetchMemberTable()
    }

    fun fetchMemberTable(query: String = "", orderColumn: MemberTableColumns? = null, ascending: Boolean = true) {
        viewModelScope.launch {
            tableState.update { it.copy(isLoading = true, order = MemberTableOrder(orderColumn, ascending)) }
            println("fetchMemberTable")
            supabase.auth.awaitInitialization()
            supabase.from("member").select() {
                if (orderColumn != null && orderColumn.id != null)
                    order(
                        orderColumn.id, if (ascending) Order.ASCENDING else Order.DESCENDING,
                        orderColumn == MemberTableColumns.Rank && ascending
                    )
            }.decodeAs<List<Member>>().also { result ->
                tableState.update { MemberTableState(false, query, MemberTableOrder(orderColumn, ascending), result) }
                println("fetchMemberTable result: $result")
            }
        }
    }

    fun createMember(name: String, isLecturer: Boolean, email: String, onResult: (DbResult) -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val member = supabase.from("member")
                    .insert(Member.New(guard = userState.value.guard!!.id, name = name, isLecturer = isLecturer)) {
                        select()
                    }.decodeSingle<Member>()
                if (email.isBlank()) {
                    onResult(DbResult.Success.WithContent(member))
                    return@launch
                }
                supabase.functions.invokeWithJsonBody("invite-user") {
                    put("member_id", member.id)
                    put("email", email)
                }
                onResult(DbResult.Success.WithContent(member))
            } catch (e: PostgrestRestException) {
                onResult(if (e.code == "42501") DbResult.Failure.PermissionDenied else DbResult.Failure.Error)
                if (e.code != "42501") e.printStackTrace()
            } catch (e: RestException) {
                onResult(if (e.statusCode == 401) DbResult.Failure.PermissionDenied else DbResult.Failure.Error)
            } catch (e: Exception) {
                onResult(DbResult.Failure.Error)
                e.printStackTrace()
            }
        }
    }
}