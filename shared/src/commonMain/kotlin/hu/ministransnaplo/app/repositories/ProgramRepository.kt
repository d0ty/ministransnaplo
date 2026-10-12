/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.repositories

import hu.ministransnaplo.app.models.Program
import hu.ministransnaplo.app.ui.components.DCFieldValue
import hu.ministransnaplo.app.util.DbResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlin.time.Instant

interface ProgramRepository {
    suspend fun fetchRange(start: Instant, end: Instant): List<Program>
    suspend fun createProgram(new: Program.New): DbResult
    suspend fun updateProgram(programId: String, data: Map<String, DCFieldValue>): DbResult
    suspend fun deleteProgram(program: Program): DbResult
}

class SupabaseProgramRepository(val supabase: SupabaseClient) : ProgramRepository {
    override suspend fun fetchRange(
        start: Instant,
        end: Instant
    ): List<Program> {
        return supabase.from("programs").select {
            order("start", Order.ASCENDING)
            filter {
                and {
                    gte("start", start)
                    lt("start", end)
                }
            }
        }.decodeAs<List<Program>>()
    }

    override suspend fun createProgram(new: Program.New): DbResult {
        supabase.from("programs").insert(new)
        return DbResult.Success.NoContent
    }

    override suspend fun updateProgram(
        programId: String,
        data: Map<String, DCFieldValue>
    ): DbResult {
        supabase.from("programs").update(data) {
            filter {
                eq("id", programId)
            }
        }
        return DbResult.Success.NoContent
    }

    override suspend fun deleteProgram(program: Program): DbResult {
        supabase.from("programs").delete {
            filter {
                eq("id", program.id)
            }
        }
        return DbResult.Success.WithContent(program)
    }
}