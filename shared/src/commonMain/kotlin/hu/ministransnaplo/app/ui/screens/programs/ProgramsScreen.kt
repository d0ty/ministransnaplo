/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.NavContainer
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.FullScreenCard
import hu.ministransnaplo.app.ui.components.ScreenTitleBar
import hu.ministransnaplo.app.ui.icons.lucide.LucideCalendarPlus
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.serialization.Serializable

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
            }
        }
    }
}