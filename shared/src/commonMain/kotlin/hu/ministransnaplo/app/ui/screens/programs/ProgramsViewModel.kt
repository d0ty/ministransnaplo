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
import hu.ministransnaplo.app.util.getStartDateOfWeek
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.Instant

class ProgramsViewModel : AppViewModel() {
    data class ViewState(
        val viewMode: ProgramsViewMode = ProgramsViewMode.MONTHLY,
        val isLoading: Boolean = true,
        val prevViewData: ViewData? = null,
        val currentViewData: ViewData? = null,
        val nextViewData: ViewData? = null
    )

    data class ViewData(
        val startDate: Instant,
        val endDate: Instant,
        val weeks: ArrayList<WeekData> = arrayListOf(),
    ) {
        init {
            val tz = TimeZone.currentSystemDefault()
            val weekNumber = startDate.until(endDate, DateTimeUnit.WEEK, tz)
            var currentWeek = getStartDateOfWeek(startDate.toLocalDateTime(tz).date)
                .atStartOfDayIn(tz)
            for (weekIdx in 0..<weekNumber) {
                weeks.add(WeekData(currentWeek))
                currentWeek = currentWeek.plus(168, DateTimeUnit.HOUR)
            }
        }

        fun add(program: Program) {
            val tz = TimeZone.currentSystemDefault()
            val week = getStartDateOfWeek(program.startDate.toLocalDateTime(tz).date)
                .atStartOfDayIn(tz)
            weeks.find { it.weekStart == week }?.programs?.add(program)
        }
    }

    data class WeekData(
        val weekStart: Instant,
        val programs: ArrayList<Program> = arrayListOf()
    )

    val calendarState: StateFlow<ViewState>
        field = MutableStateFlow(ViewState())

    init {
        fetchCalendar(ProgramsViewMode.MONTHLY, Clock.System.now())
    }

    fun fetchCalendar(viewMode: ProgramsViewMode, queryDate: Instant) {
        calendarState.update { it.copy(viewMode = viewMode, isLoading = true) }
        val queryDate = queryDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val tz = TimeZone.currentSystemDefault()
        val currentViewStartDate = when (viewMode) {
            ProgramsViewMode.MONTHLY -> {
                queryDate.minus(queryDate.day - 1, DateTimeUnit.DAY)
            }

            ProgramsViewMode.WEEKLY -> {
                val dayOfWeek = queryDate.dayOfWeek
                val daysToSubtract = if (dayOfWeek.isoDayNumber >= DayOfWeek.MONDAY.isoDayNumber) {
                    dayOfWeek.isoDayNumber - DayOfWeek.MONDAY.isoDayNumber
                } else {
                    dayOfWeek.isoDayNumber + 7 - DayOfWeek.MONDAY.isoDayNumber
                }
                queryDate.minus(daysToSubtract, DateTimeUnit.DAY)
            }
        }
        val prevViewStart =
            currentViewStartDate.minus(1, viewMode.dateUnit).atStartOfDayIn(tz)
        val nextViewEnd = currentViewStartDate
            .plus(2, viewMode.dateUnit).atStartOfDayIn(tz)
        val nextViewStart =
            currentViewStartDate.plus(1, viewMode.dateUnit).atStartOfDayIn(tz)
        val currentViewStart = currentViewStartDate.atStartOfDayIn(TimeZone.currentSystemDefault())

        viewModelScope.launch(Dispatchers.Default) {
            executeSupabaseAction {
                val data = supabase.from("programs").select {
                    order("start", Order.ASCENDING)
                    filter {
                        and {
                            gte("start", prevViewStart)
                            lte("start", nextViewEnd)
                        }
                    }
                }.decodeAs<List<Program>>()
                val prevView = ViewData(prevViewStart, currentViewStart)
                val currentView = ViewData(currentViewStart, nextViewEnd)
                val nextView = ViewData(nextViewStart, nextViewEnd)
                data.forEach {
                    if (it.startDate in prevViewStart..<currentViewStart) prevView.add(it)
                    else if (it.startDate in currentViewStart..<nextViewStart) currentView.add(it)
                    else nextView.add(it)
                }
                calendarState.update {
                    it.copy(
                        isLoading = false,
                        prevViewData = prevView,
                        currentViewData = currentView,
                        nextViewData = nextView
                    )
                }
                return@executeSupabaseAction DbResult.Success.NoContent
            }
        }
    }

    fun createProgram(
        title: String,
        description: String,
        start: Long,
        end: Long,
        mandatory: Boolean,
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
                        mandatory = mandatory
                    )
                )
                return@executeSupabaseAction DbResult.Success.NoContent
            }.also(onResult)
        }
    }
}