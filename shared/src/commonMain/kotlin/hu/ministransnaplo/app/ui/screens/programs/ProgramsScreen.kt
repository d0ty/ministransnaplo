/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.NavContainer
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.Theme
import hu.ministransnaplo.app.ui.components.FullScreenCard
import hu.ministransnaplo.app.ui.components.ScreenTitleBar
import hu.ministransnaplo.app.ui.icons.lucide.LucideCalendarPlus
import hu.ministransnaplo.app.ui.icons.lucide.LucideChevronLeft
import hu.ministransnaplo.app.ui.icons.lucide.LucideChevronRight
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
object Programs : NavItem

@Serializable
object ProgramsRoot : NavItem

enum class ProgramsViewMode(val dateUnit: DateTimeUnit.DateBased) {
    MONTHLY(DateTimeUnit.MONTH),
    WEEKLY(DateTimeUnit.WEEK),
}

@Composable
fun ProgramsScreen(onNavigation: (NavItem) -> Unit, viewModel: ProgramsViewModel) {
    val userState by viewModel.userState.collectAsState()
    val calendarState by viewModel.calendarState.collectAsState()
    viewModel.viewModelScope.launch {
        viewModel.calendarState.collect {
            println("CalendarState is ${if (!it.isLoading) "not" else ""} loading")
            println("Current view mode: ${it.viewMode}")
            println("Query time: ${it.prevViewData?.startDate} - ${it.nextViewData?.endDate}")
            mapOf(
                "prev" to it.prevViewData,
                "current" to it.currentViewData,
                "next" to it.nextViewData
            ).forEach { (key, viewData) ->
                if (viewData != null) {
                    println("$key view data: ${viewData.startDate} - ${viewData.endDate}")
                    println("Weeks: ${viewData.weeks.size}")
                    viewData.weeks.forEachIndexed { index, week ->
                        println("Week $index: ${week.weekStart}, programs: ${week.programs.size}")
                        week.programs.forEach { program ->
                            println("Program: ID: ${program.id}, title: ${program.title}, start: ${program.startDate}, duration: ${program.duration}")
                        }
                    }
                } else {
                    println("$key view data is null")
                }
                println()
            }
            println("-------------------------------------------")
        }
    }
    NavContainer(onNavigation = onNavigation) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitleBar("Programok", LucideCalendarPlus, userState.isGuardOwner) {
                onNavigation(NewProgram)
            }
            Spacer(modifier = Modifier.height(16.dp))
            FullScreenCard(Modifier.fillMaxSize().fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextButton(onClick = {
                        if (calendarState.viewMode != ProgramsViewMode.MONTHLY) viewModel.fetchCalendar(
                            ProgramsViewMode.MONTHLY,
                            calendarState.currentViewData?.startDate!!
                        )
                    }) {
                        Text(
                            "Hónap",
                            textDecoration = if (calendarState.viewMode == ProgramsViewMode.MONTHLY) TextDecoration.Underline else null,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                        )
                    }
                    TextButton(onClick = {
                        if (calendarState.viewMode != ProgramsViewMode.WEEKLY) viewModel.fetchCalendar(
                            ProgramsViewMode.WEEKLY,
                            calendarState.currentViewData?.startDate!!
                        )
                    }) {
                        Text(
                            "Hét",
                            textDecoration = if (calendarState.viewMode == ProgramsViewMode.WEEKLY) TextDecoration.Underline else null,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                        )
                    }

                }
                val currentView = calendarState.currentViewData
                if (currentView == null) {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center).size(36.dp))
                    }
                } else {
                    Column {
                        Row {
                            Text(
                                currentView.title,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                            Spacer(Modifier.width(24.dp))
                            Image(
                                LucideChevronLeft,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp).clickable {
                                    // TODO: implement calendar navigation
                                },
                            )
                            Spacer(Modifier.width(8.dp))
                            Image(
                                LucideChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp).clickable {
                                    // TODO: implement calendar navigation
                                },
                            )
                        }
                        Spacer(Modifier.height(32.dp))
                        when (calendarState.viewMode) {
                            ProgramsViewMode.MONTHLY -> MonthlyProgramsView(currentView) {
                                // TODO: handle navigation
                            }

                            ProgramsViewMode.WEEKLY -> WeeklyProgramsView(currentView) {
                                // TODO: handle navigation
                            }
                        }
                    }
                }
            }
        }
    }
}

expect fun getDaysOfWeek(): Array<String>

@Composable
fun MonthlyProgramsView(viewData: ProgramsViewModel.ViewData, onNavigation: (NavItem) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            getDaysOfWeek().forEach {
                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.Center) {
                    Text(it)
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Column(Modifier.fillMaxSize()) {
            val tz = TimeZone.currentSystemDefault()
            viewData.weeks.forEachIndexed { weekIdx, week ->
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize().weight(1f)) {
                    val startDate = week.weekStart.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    week.getCategorizedPrograms().forEachIndexed { index, dailyProgram ->
                        Column(
                            Modifier.padding(4.dp).clip(RoundedCornerShape(4.dp))
                                .background(color = Theme.colorScheme.background).fillMaxSize().weight(1f),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val date = startDate.plus(index, DateTimeUnit.DAY)
                            Text(
                                date.day.toString(),
                                fontWeight = FontWeight.Bold,
                                color = if (weekIdx in 1..3 || (weekIdx == 4 && startDate.month == date.month) || (weekIdx == 0 && startDate.month != date.month)) Color.White else Color.Gray,
                                modifier = if (Clock.System.todayIn(tz) == date) Modifier.padding(12.dp, 4.dp)
                                    .clip(RoundedCornerShape(4.dp)).background(Theme.colorScheme.surface) else Modifier
                            )
                            Spacer(Modifier.height(4.dp))
                            for (program in dailyProgram) {
                                Row(
                                    Modifier.padding(4.dp).clip(RoundedCornerShape(4.dp)).background(program.color)
                                        .fillMaxWidth().clickable {
                                            // TODO: navigate to event details
                                        }) {
                                    Text(
                                        program.title,
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(4.dp, 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
expect fun WeeklyProgramsView(viewData: ProgramsViewModel.ViewData, onNavigation: (NavItem) -> Unit)