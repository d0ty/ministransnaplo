/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.models.Program
import hu.ministransnaplo.app.util.DbResult
import hu.ministransnaplo.app.util.calculateInterval
import hu.ministransnaplo.app.util.executeSupabaseAction
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlin.time.Instant

class ProgramsViewModel : AppViewModel() {

    fun createProgram(
        title: String,
        description: String,
        start: Long,
        end: Long,
        mandantory: Boolean,
        onResult: (result: DbResult) -> Unit
    ) {
        val duration = calculateInterval(start, end)
        viewModelScope.launch {
            executeSupabaseAction {
                supabase.from("programs").insert(
                    Program.New(
                        guard = userState.value.guard!!.id,
                        title = title,
                        description = description,
                        startDate = Instant.fromEpochMilliseconds(start),
                        duration = duration,
                        mandatory = mandantory
                    )
                )
                return@executeSupabaseAction DbResult.Success.NoContent
            }.also(onResult)
        }
    }
}