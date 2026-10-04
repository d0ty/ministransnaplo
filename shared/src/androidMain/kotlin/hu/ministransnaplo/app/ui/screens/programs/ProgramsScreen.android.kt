/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.ministransnaplo.app.ui.NavItem
import kotlinx.datetime.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
actual fun WeeklyProgramsView(
    viewData: ProgramsViewModel.ViewData,
    onNavigation: (NavItem) -> Unit
) {
    val tz = TimeZone.currentSystemDefault()
    val startDate = viewData.startDate.toLocalDateTime(tz).date
    val currentWeek = viewData.weeks[0]
    val scrollState = rememberScrollState()
    Column(Modifier.fillMaxSize().verticalScroll(scrollState)) {
        if (currentWeek.programs.isEmpty()) {
            Text("Ezen a héten nincsen program", Modifier.align(Alignment.CenterHorizontally))
        }
        currentWeek.getCategorizedPrograms().forEachIndexed { dayIdx, dailyPrograms ->
            val date = startDate.plus(dayIdx, DateTimeUnit.DAY)
            val dateFormatter = LocalDate.Format { byUnicodePattern("MM.dd.") }
            val timeFormatter = LocalTime.Format { byUnicodePattern("HH:mm") }
            if (dailyPrograms.isNotEmpty()) Column(Modifier.fillMaxWidth()) {
                Text(
                    "${dateFormatter.format(date)} ${getDaysOfWeek()[dayIdx]}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                dailyPrograms.forEach {
                    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(it.color).padding(4.dp)) {
                        Text(it.title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        val startTime = it.startDate.toLocalDateTime(tz).time
                        val durationInMinutes = it.duration.hour * 60 + it.duration.minute
                        val endTime = it.startDate.plus(durationInMinutes, DateTimeUnit.MINUTE).toLocalDateTime(tz).time
                        Text(
                            "${timeFormatter.format(startTime)} - ${timeFormatter.format(endTime)}",
                            fontWeight = FontWeight.Medium
                        )
                        Text(it.description)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}