/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.Theme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

@Composable
actual fun WeeklyProgramsView(
    viewData: ProgramsViewModel.ViewData,
    onNavigation: (NavItem) -> Unit
) {
    val currentWeek = viewData.weeks[0]
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Row(Modifier.width(75.dp)) {}
                getDaysOfWeek().forEachIndexed { idx, day ->
                    Column(Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            day,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        )
                        val startDate = currentWeek.weekStart.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        Text(
                            startDate.plus(idx, DateTimeUnit.DAY).day.toString(),
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Column {
                for (hour in 5..23) {
                    Row(Modifier.fillMaxHeight().weight(1f)) {
                        Text("${if (hour < 10) "0" else ""}$hour:00", Modifier.width(75.dp))
                    }
                }
            }
        }
        WeekGrid(Modifier.padding(start = 75.dp, top = 60.dp).background(Theme.colorScheme.background)) {
            currentWeek.getCategorizedPrograms().forEachIndexed { idx, dailyPrograms ->
                dailyPrograms.forEach { program ->
                    val programStartTime = program.startDate.toLocalDateTime(TimeZone.currentSystemDefault()).time
                    val programStartMinute = programStartTime.hour * 60 + programStartTime.minute
                    val programDurationMinute = program.duration.hour * 60 + program.duration.minute
                    Column(
                        Modifier.fillMaxSize().weekSlot(
                            idx, programStartMinute, programStartMinute + programDurationMinute
                        ).background(program.color).padding(2.dp, 4.dp)
                    ) {
                        Text(program.title, fontWeight = FontWeight.Bold)
                        val programEndTime =
                            program.startDate.plus(programDurationMinute, DateTimeUnit.MINUTE)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).time
                        if (programDurationMinute > 60) Text(
                            "${programStartTime.hour.toString().padStart(2, '0')}:${
                                programStartTime.minute.toString().padStart(2, '0')
                            } - ${programEndTime.hour.toString().padStart(2, '0')}:${
                                programEndTime.minute.toString().padStart(2, '0')
                            }",
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        }
    }
}

private class SlotData(val dayIndex: Int, val startMin: Int, val endMin: Int)

fun Modifier.weekSlot(dayIndex: Int, startMin: Int, endMin: Int) =
    this.then(object : ParentDataModifier {
        override fun Density.modifyParentData(parentData: Any?) =
            SlotData(dayIndex, startMin, endMin)
    })

@Composable
fun WeekGrid(
    modifier: Modifier = Modifier,
    startHour: Int = 5,
    endHour: Int = 24,
    hourHeight: Dp = 36.dp,
    lineColor: Color = Color.Gray,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier.drawBehind {
            val h = hourHeight.toPx()
            val colW = size.width / 7
            for (i in 0..(endHour - startHour)) {                // horizontal lines
                drawLine(lineColor, Offset(0f, i * h), Offset(size.width, i * h))
            }
            for (d in 0..7) {                                     // vertical lines
                drawLine(lineColor, Offset(d * colW, 0f), Offset(d * colW, size.height))
            }
        }
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val colW = width / 7
        val hourPx = hourHeight.toPx()
        val gridStartMin = startHour * 60

        val placed = measurables.map { m ->
            val d = m.parentData as SlotData
            val top = ((d.startMin - gridStartMin) / 60f * hourPx).roundToInt()
            val bottom = ((d.endMin - gridStartMin) / 60f * hourPx).roundToInt()
            val p = m.measure(Constraints.fixed(colW, bottom - top))
            Triple(p, d.dayIndex * colW, top)
        }

        layout(width, (hourPx * (endHour - startHour)).roundToInt()) {
            placed.forEach { (p, x, y) -> p.placeRelative(x, y) }
        }
    }
}
