/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.models.Justification
import hu.ministransnaplo.app.models.Member
import hu.ministransnaplo.app.repositories.AuthRepository
import hu.ministransnaplo.app.repositories.JustificationRepository
import hu.ministransnaplo.app.repositories.MemberRepository
import hu.ministransnaplo.app.ui.components.DCFieldValue
import hu.ministransnaplo.app.util.DbResult
import hu.ministransnaplo.app.util.executeSupabaseAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MembersViewModel(
    authRepo: AuthRepository,
    val memberRepo: MemberRepository,
    val justificationRepository: JustificationRepository
) : AppViewModel(authRepo) {
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

    val omittedPrograms: StateFlow<List<Justification.WithProgram>>
        field = MutableStateFlow(emptyList())

    init {
        fetchMemberTable()
    }

    fun selectMember(member: Member) {
        println("Selected member: ${Json.encodeToString(member)}")
        currentMember.update { member }
        fetchMemberOmissions()
    }

    private fun fetchMemberOmissions() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = executeSupabaseAction {
                DbResult.Success.WithContent(justificationRepository.getOmissionsByMember(currentMember.value!!.id))
            }
            if (result is DbResult.Success.WithContent<*>)
                omittedPrograms.update { result.result as List<Justification.WithProgram> }
        }
    }

    fun fetchMemberTable(query: String = "", orderColumn: MemberTableColumns? = null, ascending: Boolean = true) {
        viewModelScope.launch {
            tableState.update { it.copy(isLoading = true, order = MemberTableOrder(orderColumn, ascending)) }
            authRepo.awaitAuthInit()
            memberRepo.fetchTableData(query, orderColumn, ascending).also { result ->
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

    fun createMember(name: String, isLecturer: Boolean, email: String, onResult: (DbResult) -> Unit) {
        val newMember =
            Member.New(guard = userState.value.userOrNull!!.guard!!.id, name = name, isLecturer = isLecturer)
        viewModelScope.launch(Dispatchers.Default) {
            executeSupabaseAction {
                return@executeSupabaseAction memberRepo.createMember(newMember, email)
            }.also { fetchMemberTable() }.also(onResult)
        }
    }

    fun updateMember(data: Map<String, DCFieldValue>, onResult: (DbResult) -> Unit) {
        if (currentMember.value == null) throw IllegalStateException("Can't update no member")
        viewModelScope.launch(Dispatchers.Default) {
            memberRepo.updateMember(currentMember.value!!.id, data).also { result ->
                if (result is DbResult.Success.WithContent<*>) {
                    currentMember.update { (result.result as Member).copy(email = result.result.email) }
                }
            }.also(onResult)
        }
    }

    fun inviteLeader(email: String, onResult: suspend (DbResult) -> Unit) {
        if (currentMember.value == null) throw IllegalStateException("Can't invite leader without a current member selected")
        viewModelScope.launch(Dispatchers.Default) {
            val result = executeSupabaseAction {
                memberRepo.inviteLeader(currentMember.value!!, email)
            }
            fetchMemberTable()
            onResult(result)
        }
    }

    fun deleteMember(onResult: suspend (DbResult) -> Unit) {
        if (currentMember.value == null) throw IllegalStateException("Can't delete member without a current member selected")
        viewModelScope.launch(Dispatchers.Default) {
            val result = executeSupabaseAction {
                val result = memberRepo.deleteMember(currentMember.value!!)
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
                memberRepo.resetLeaderMFA(currentMember.value!!)
            }
            onResult(result)
        }
    }

    fun justifyProgramOmission(programId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val res = justificationRepository.createJustification(
                programId,
                currentMember.value!!.id,
                userState.value.userOrNull!!.guard!!.id
            )
            if (res is DbResult.Success) {
                fetchMemberOmissions()
                fetchMemberTable()
            }
        }
    }
}