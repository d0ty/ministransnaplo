/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.repositories

import hu.ministransnaplo.app.models.Member
import hu.ministransnaplo.app.ui.components.DCFieldValue
import hu.ministransnaplo.app.ui.screens.members.MemberTableColumns
import hu.ministransnaplo.app.util.DbResult
import hu.ministransnaplo.app.util.executeSupabaseAction
import hu.ministransnaplo.app.util.invokeWithJsonBody
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.http.*
import kotlinx.serialization.json.put

interface MemberRepository {
    suspend fun fetchTableData(
        query: String,
        orderColumn: MemberTableColumns? = null,
        ascending: Boolean = true
    ): List<Member>

    suspend fun inviteLeader(member: Member, email: String): DbResult
    suspend fun createMember(newMember: Member.New, email: String): DbResult
    suspend fun updateMember(memberId: String, data: Map<String, DCFieldValue>): DbResult
    suspend fun deleteMember(member: Member): DbResult
    suspend fun resetLeaderMFA(member: Member): DbResult
}

class SupabaseMemberRepository(val supabase: SupabaseClient) : MemberRepository {
    override suspend fun fetchTableData(
        query: String,
        orderColumn: MemberTableColumns?,
        ascending: Boolean
    ): List<Member> {
        return supabase.from("member_data").select {
            if (orderColumn != null && orderColumn.id != null)
                order(
                    orderColumn.id, if (ascending) Order.ASCENDING else Order.DESCENDING,
                    orderColumn == MemberTableColumns.Rank && ascending
                )
            if (query.isNotBlank()) filter {
                like("name", "%$query%")
            }
        }.decodeAs<List<Member>>()
    }

    override suspend fun inviteLeader(
        member: Member,
        email: String
    ): DbResult {
        supabase.functions.invokeWithJsonBody("invite-user") {
            put("member_id", member.id)
            put("email", email)
        }
        return DbResult.Success.NoContent
    }

    override suspend fun createMember(
        newMember: Member.New,
        email: String
    ): DbResult {
        val member = supabase.from("member")
            .insert(newMember) {
                select()
            }.decodeSingle<Member>()
        if (email.isBlank()) {
            return DbResult.Success.WithContent(member)
        }
        inviteLeader(member, email)
        return DbResult.Success.WithContent(member)
    }

    override suspend fun updateMember(
        memberId: String,
        data: Map<String, DCFieldValue>
    ): DbResult {
        return executeSupabaseAction {
            val res = supabase.from("member").update(data) {
                select()
                filter {
                    eq("id", memberId)
                }
            }.decodeSingle<Member>()
            return@executeSupabaseAction DbResult.Success.WithContent(res)
        }
    }

    override suspend fun deleteMember(member: Member): DbResult {
        if (member.isLeader) {
            val userDeleteResp = supabase.functions.invokeWithJsonBody("delete-user") {
                put("member_login", member.userId)
            }
            if (!userDeleteResp.status.isSuccess()) {
                return DbResult.Failure.Error
            }
        }
        supabase.from("member").delete {
            filter {
                eq("id", member.id)
            }
        }
        return DbResult.Success.WithContent(member)
    }

    override suspend fun resetLeaderMFA(member: Member): DbResult {
        return supabase.functions.invokeWithJsonBody("reset-mfa") {
            put("user_id", member.userId)
        }.status.isSuccess().let {
            if (it) DbResult.Success.NoContent else DbResult.Failure.Error
        }
    }
}