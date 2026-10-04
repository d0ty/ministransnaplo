/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.ministransnaplo.app.NavContainer
import hu.ministransnaplo.app.getPlatform
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

private const val PAGE_COUNT = 20_000
private const val START = PAGE_COUNT / 2

@Composable
fun ProgramsScreen(onNavigation: (NavItem) -> Unit, viewModel: ProgramsViewModel) {
    val userState by viewModel.userState.collectAsState()
    val calendarState by viewModel.calendarState.collectAsState()
    val pagerState = rememberPagerState(initialPage = START, pageCount = { PAGE_COUNT })
    val scope = rememberCoroutineScope()
    NavContainer(onNavigation = onNavigation) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitleBar("Programok", LucideCalendarPlus, userState.isGuardOwner) {
                onNavigation(NewProgram)
            }
            Spacer(modifier = Modifier.height(16.dp))
            FullScreenCard(Modifier.fillMaxSize().fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

                    fun setViewMode(mode: ProgramsViewMode) {
                        viewModel.setViewMode(mode)
                        if (pagerState.settledPage == START) viewModel.onPageSettled(0)
                        else scope.launch {
                            pagerState.animateScrollToPage(START)
                        }
                    }

                    TextButton(onClick = {
                        if (calendarState.viewMode != ProgramsViewMode.MONTHLY) setViewMode(ProgramsViewMode.MONTHLY)
                    }) {
                        Text(
                            "Hónap",
                            textDecoration = if (calendarState.viewMode == ProgramsViewMode.MONTHLY) TextDecoration.Underline else null,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                    TextButton(onClick = {
                        if (calendarState.viewMode != ProgramsViewMode.WEEKLY) setViewMode(ProgramsViewMode.WEEKLY)
                    }) {
                        Text(
                            "Hét",
                            textDecoration = if (calendarState.viewMode == ProgramsViewMode.WEEKLY) TextDecoration.Underline else null,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }

                }
                fun swipe(forward: Boolean) {
                    scope.launch {
                        val target = pagerState.currentPage + if (forward) 1 else -1
                        pagerState.animateScrollToPage(target)
                    }
                }
                HorizontalPager(
                    pagerState,
                    Modifier.weight(1f),
                    key = { it }
                ) { idx ->
                    val data = calendarState.views[idx - START]
                    if (data != null) ViewHandler(calendarState.viewMode, data, onNavigation) { swipe(it) }
                    else Box(Modifier.fillMaxSize().clipToBounds()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center).size(36.dp))
                    }
                }
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.settledPage }
                        .collect { page ->
                            viewModel.onPageSettled(page - START)
                        }
                }
            }
        }
    }
}

fun getDaysOfWeekShort(): Array<String> =
    arrayOf("H", "K", "Sze", "Cs", "P", "Szo", "V")

fun getDaysOfWeek(): Array<String> =
    arrayOf("Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek", "Szombat", "Vasárnap")


@Composable
fun ViewHandler(
    viewMode: ProgramsViewMode,
    viewData: ProgramsViewModel.ViewData,
    onNavigation: (NavItem) -> Unit,
    onSwipe: (forward: Boolean) -> Unit
) {
    Column(Modifier.fillMaxSize().clipToBounds()) {
        Row {
            Text(
                viewData.title,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 20.sp
            )
            Spacer(Modifier.width(24.dp))
            if (!getPlatform().isMobile) Row {
                Image(
                    LucideChevronLeft,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).clickable {
                        onSwipe(false)
                    },
                )
                Spacer(Modifier.width(8.dp))
                Image(
                    LucideChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).clickable {
                        onSwipe(true)
                    },
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        when (viewMode) {
            ProgramsViewMode.MONTHLY -> MonthlyProgramsView(viewData, onNavigation)
            ProgramsViewMode.WEEKLY -> WeeklyProgramsView(viewData, onNavigation)
        }
    }
}


@Composable
fun MonthlyProgramsView(viewData: ProgramsViewModel.ViewData, onNavigation: (NavItem) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            (if (getPlatform().isMobile) getDaysOfWeekShort() else getDaysOfWeek()).forEach {
                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.Center) {
                    Text(it)
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Column(Modifier.fillMaxSize()) {
            val tz = TimeZone.currentSystemDefault()
            val dayPadding = when (getPlatform().isMobile) {
                true -> 1
                false -> 4
            }.dp
            viewData.weeks.forEachIndexed { weekIdx, week ->
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize().weight(1f)) {
                    val startDate = week.weekStart.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    week.getCategorizedPrograms().forEachIndexed { index, dailyProgram ->
                        Column(
                            Modifier.padding(dayPadding).clip(RoundedCornerShape(dayPadding))
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
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Theme.colorScheme.surface) else Modifier
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