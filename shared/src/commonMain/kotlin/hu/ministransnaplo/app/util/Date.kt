/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.util

import kotlinx.datetime.*
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlin.time.Instant

@OptIn(FormatStringsInDatetimeFormats::class)
fun convertMillisToTimeString(millis: Long): String {
    val formatter = DateTimeComponents.Format {
        byUnicodePattern("HH:mm")
    }
    return Instant.fromEpochMilliseconds(millis).format(formatter)
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun convertMillisToDateString(millis: Long): String {
    val formatter = DateTimeComponents.Format {
        byUnicodePattern("yyyy.MM.dd")
    }
    return Instant.fromEpochMilliseconds(millis).format(formatter)
}

/**
 * Calculates postgres intervals
 */
fun calculateInterval(start: Long, end: Long): String {
    val duration = end - start
    val days = duration / (1000 * 60 * 60 * 24)
    val hours = duration / (1000 * 60 * 60) % 24
    val minutes = duration / (1000 * 60) % 60
    val seconds = duration / 1000 % 60
    return "$days days $hours hours $minutes minutes $seconds seconds"
}

fun getStartDateOfWeek(date: LocalDate): LocalDate {
    val dayOfWeek = date.dayOfWeek
    val daysToSubtract = if (dayOfWeek.isoDayNumber >= DayOfWeek.MONDAY.isoDayNumber) {
        dayOfWeek.isoDayNumber - DayOfWeek.MONDAY.isoDayNumber
    } else {
        dayOfWeek.isoDayNumber + 7 - DayOfWeek.MONDAY.isoDayNumber
    }
    return date.minus(daysToSubtract, DateTimeUnit.DAY)
}

fun getMonthName(month: Month) = arrayOf(
    "január", "február", "március", "árpilis",
    "május", "június", "július", "augusztus",
    "szeptember", "október", "november", "december"
)[month.ordinal]