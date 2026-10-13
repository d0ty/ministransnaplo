/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.repositories

import hu.ministransnaplo.app.models.Justification
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

interface JustificationRepository {
    suspend fun getByProgram(programId: String): List<Justification.WithMember>
}

class SupabaseJustificationRepository(val supabase: SupabaseClient) : JustificationRepository {
    override suspend fun getByProgram(programId: String): List<Justification.WithMember> {
        return supabase.postgrest.rpc(
            "get_justifications_for_program",
            mapOf("programId" to programId)
        ).decodeAs<List<Justification.WithMember>>()
    }
}