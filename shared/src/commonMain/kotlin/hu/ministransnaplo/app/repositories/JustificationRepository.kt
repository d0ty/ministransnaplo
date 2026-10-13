/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.repositories

import hu.ministransnaplo.app.models.Justification
import hu.ministransnaplo.app.util.DbResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

interface JustificationRepository {
    suspend fun getByProgram(programId: String): List<Justification.WithMember>
    suspend fun createJustification(programId: String, memberId: String, guardId: String): DbResult
    suspend fun deleteJustification(id: String): DbResult
}

class SupabaseJustificationRepository(val supabase: SupabaseClient) : JustificationRepository {
    override suspend fun getByProgram(programId: String): List<Justification.WithMember> {
        return supabase.postgrest.rpc(
            "get_justifications_for_program",
            mapOf("programId" to programId)
        ).decodeAs<List<Justification.WithMember>>()
    }

    override suspend fun createJustification(
        programId: String,
        memberId: String,
        guardId: String
    ): DbResult {
        supabase.from("justification").insert(mapOf("program" to programId, "member" to memberId, "guard" to guardId))
        return DbResult.Success.NoContent
    }

    override suspend fun deleteJustification(id: String): DbResult {
        supabase.from("justification").delete { filter { eq("id", id) } }
        return DbResult.Success.NoContent
    }
}