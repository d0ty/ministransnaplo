/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.models.Justification
import hu.ministransnaplo.app.models.Program
import hu.ministransnaplo.app.repositories.AuthRepository
import hu.ministransnaplo.app.repositories.JustificationRepository
import hu.ministransnaplo.app.repositories.ProgramRepository
import hu.ministransnaplo.app.ui.components.DCFieldValue
import hu.ministransnaplo.app.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import kotlin.concurrent.Volatile
import kotlin.time.Clock
import kotlin.time.Instant

class ProgramsViewModel(
    val programRepo: ProgramRepository,
    val justificationRepo: JustificationRepository,
    authRepo: AuthRepository
) : AppViewModel(authRepo) {

    data class ViewState(
        val viewMode: ProgramsViewMode = ProgramsViewMode.MONTHLY,
        val anchorStart: LocalDate,
        val views: Map<Int, ViewData> = emptyMap(),
    )

    class ViewData(
        viewMode: ProgramsViewMode,
        _startDate: Instant,
        _endDate: Instant,
        _weeks: ArrayList<WeekData> = arrayListOf(),
    ) {
        var title: String = ""
            private set
        val startDate = _startDate
        val endDate = _endDate
        val weeks: ArrayList<WeekData> = _weeks


        init {
            val tz = TimeZone.currentSystemDefault()
            val weekNumber = startDate.until(endDate, DateTimeUnit.WEEK, tz) + 1
            var currentWeek = getStartDateOfWeek(startDate.toLocalDateTime(tz).date)
                .atStartOfDayIn(tz)
            for (weekIdx in 0..<weekNumber) {
                weeks.add(WeekData(currentWeek))
                currentWeek = currentWeek.plus(168, DateTimeUnit.HOUR)
            }
            val startLocalDate = startDate.toLocalDateTime(tz)
            val endLocalDate = endDate.toLocalDateTime(tz)
            title = "${startLocalDate.year}. ${getMonthName(startLocalDate.month).capitalize(Locale.current)} ${
                if (viewMode == ProgramsViewMode.WEEKLY) "${startLocalDate.day} - ${
                    if (endLocalDate.month != startLocalDate.month) getMonthName(endLocalDate.month).capitalize(Locale.current) + " " else ""
                } ${endLocalDate.day}." else ""
            }"
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
    ) {
        fun getCategorizedPrograms(): ArrayList<List<Program>> {
            val output = arrayListOf<List<Program>>()
            for (dayOfWeek in DayOfWeek.entries) {
                output.add(programs.filter { it.startDate.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek == dayOfWeek })
            }
            return output
        }
    }

    companion object {
        private const val CHUNK = 6
        private const val KEEP = CHUNK + 2
    }


    val calendarState: StateFlow<ViewState>
        field = MutableStateFlow(ViewState(anchorStart = periodStartOf(ProgramsViewMode.MONTHLY, today())))

    val currentProgram: StateFlow<Program?>
        field = MutableStateFlow(null)

    val currentJustifications: StateFlow<List<Justification.WithMember>>
        field = MutableStateFlow(emptyList())

    private val requested = mutableSetOf<Int>()

    @Volatile
    private var currentOffset = 0

    @Volatile
    private var epoch = 0

    private val tz get() = TimeZone.currentSystemDefault()
    private fun today() = Clock.System.todayIn(tz)

    fun selectProgram(program: Program?) {
        currentProgram.update { program }
        if (program != null) fetchCurrentJustifications()
        else currentJustifications.update { emptyList() }
    }

    private fun fetchCurrentJustifications() {
        viewModelScope.launch {
            val justifications = executeSupabaseAction {
                DbResult.Success.WithContent(justificationRepo.getByProgram(currentProgram.value!!.id))
            }
            if (justifications is DbResult.Success.WithContent<*>) {
                currentJustifications.update { justifications.result as List<Justification.WithMember> }
            }
        }
    }

    private fun periodStartOf(viewMode: ProgramsViewMode, date: LocalDate) = when (viewMode) {
        ProgramsViewMode.MONTHLY -> LocalDate(date.year, date.month, 1)
        ProgramsViewMode.WEEKLY -> date.minus(date.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
    }

    private fun periodStart(viewMode: ProgramsViewMode, anchor: LocalDate, offset: Int) =
        anchor.plus(offset, viewMode.dateUnit)

    private fun chunkOf(offset: Int) = (offset + CHUNK / 2).floorDiv(CHUNK)

    fun setViewMode(viewMode: ProgramsViewMode) {
        val s = calendarState.value
        val day = periodStart(s.viewMode, s.anchorStart, currentOffset)
        reset(viewMode, periodStartOf(viewMode, day))
    }

    private fun reset(
        viewMode: ProgramsViewMode,
        anchor: LocalDate
    ) {
        println("CAL anchor=$anchor mode=$viewMode")
        epoch++
        requested.clear()
        currentOffset = 0
        calendarState.update { it.copy(viewMode = viewMode, anchorStart = anchor, views = emptyMap()) }
    }

    private fun reload() {
        epoch++
        requested.clear()
        val window = (currentOffset - 1..currentOffset + 1).map(::chunkOf)
        calendarState.update { s -> s.copy(views = s.views.filterKeys { chunkOf(it) in window }) }
        onPageSettled(currentOffset)
    }

    fun onPageSettled(offset: Int) {
        currentOffset = offset
        evict(offset)
        (offset - 1..offset + 1)
            .map { it.floorDiv(CHUNK) }
            .distinct()
            .forEach(::loadChunk)
    }

    private fun loadChunk(chunk: Int) {
        if (!requested.add(chunk)) return

        val snapshot = calendarState.value
        val mode = snapshot.viewMode
        val myEpoch = epoch
        val anchor = snapshot.anchorStart
        val from = chunk * CHUNK

        viewModelScope.launch(Dispatchers.Default) {
            println("Loading chunk $chunk, from=$from")
            val result = executeSupabaseAction {
                val loaded = fetchRange(mode, anchor, from, from + CHUNK - 1)
                calendarState.update { s ->
                    if (s.viewMode != mode || s.anchorStart != anchor || epoch != myEpoch) s
                    else {
                        val lo = currentOffset - KEEP
                        val hi = currentOffset + KEEP
                        s.copy(views = (s.views + loaded).filterKeys { it in lo..hi })
                    }
                }
                DbResult.Success.NoContent
            }
            println("Loaded chunk $chunk, from=$from, result=$result")
            val stillCurrent = calendarState.value.let { it.viewMode == mode && it.anchorStart == anchor }
            if (stillCurrent && result is DbResult.Failure) requested.remove(chunk)
        }
    }

    private suspend fun fetchRange(
        viewMode: ProgramsViewMode,
        anchor: LocalDate,
        fromOffset: Int,
        toOffset: Int
    ): Map<Int, ViewData> =
        withContext(Dispatchers.Default) {
            val bounds = (fromOffset..toOffset + 1)
                .map { periodStart(viewMode, anchor, it).atStartOfDayIn(tz) }
            println("CAL bounds=" + bounds.map { it.toLocalDateTime(tz).date })

            val views = (fromOffset..toOffset).associateWith { offset ->
                val i = offset - fromOffset
                ViewData(viewMode, bounds[i], bounds[i + 1])
            }

            val data = programRepo.fetchRange(bounds.first(), bounds.last())

            var idx = 0
            for (program in data) {
                while (idx < bounds.size - 2 && program.startDate >= bounds[idx + 1]) idx++
                views.getValue(fromOffset + idx).add(program)
            }

            views
        }

    fun evict(center: Int) {
        val lo = center - KEEP
        val hi = center + KEEP
        calendarState.update { s -> s.copy(views = s.views.filterKeys { it in lo..hi }) }
        requested.retainAll { it in lo.floorDiv(CHUNK)..hi.floorDiv(CHUNK) }
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
        val newProgram = Program.New(
            guard = userState.value.userOrNull!!.guard!!.id,
            title = title,
            description = description,
            startDate = Instant.fromEpochMilliseconds(start),
            duration = duration,
            mandatory = mandatory
        )

        viewModelScope.launch {
            executeSupabaseAction {
                return@executeSupabaseAction programRepo.createProgram(newProgram)
            }.also(onResult)
            reload()
        }
    }

    fun updateProgram(data: Map<String, DCFieldValue>, onResult: (DbResult) -> Unit) {
        if (currentProgram.value == null) throw IllegalStateException("Can't update no program")
        viewModelScope.launch(Dispatchers.Default) {
            executeSupabaseAction {
                return@executeSupabaseAction programRepo.updateProgram(currentProgram.value!!.id, data)
            }.also(onResult)
            reload()
        }
    }

    fun deleteMember(onResult: suspend (DbResult) -> Unit) {
        if (currentProgram.value == null) throw IllegalStateException("Can't delete program without a current program selected")
        viewModelScope.launch(Dispatchers.Default) {
            val result = executeSupabaseAction {
                val result = programRepo.deleteProgram(currentProgram.value!!)
                currentProgram.update { null }
                result
            }
            reload()
            onResult(result)
        }
    }

    fun toggleMemberJustification(memberId: String) {
        if (currentProgram.value == null) throw IllegalStateException("Can't toggle justification without a current program selected")
        if (currentJustifications.value.isEmpty()) throw IllegalStateException("Can't toggle justification without loaded justifications")
        viewModelScope.launch(Dispatchers.Default) {
            val result = executeSupabaseAction {
                val currentJustifications = currentJustifications.value
                val existing = currentJustifications.find { it.memberId == memberId && it.justificationId != null }
                if (existing != null) {
                    TODO("Justification deletion")
                } else {
                    justificationRepo.createJustification(
                        currentProgram.value!!.id,
                        memberId,
                        userState.value.userOrNull!!.guard!!.id
                    )
                }
            }
            if (result is DbResult.Success) fetchCurrentJustifications()
        }
    }
}