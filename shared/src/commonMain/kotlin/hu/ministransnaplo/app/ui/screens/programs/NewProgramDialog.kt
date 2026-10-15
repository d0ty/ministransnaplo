/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.*
import hu.ministransnaplo.app.ui.icons.lucide.LucideClock4
import hu.ministransnaplo.app.ui.icons.lucide.LucideSquarePen
import hu.ministransnaplo.app.ui.icons.lucide.LucideUsers
import hu.ministransnaplo.app.util.DbResult
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@Serializable
object NewProgram : NavItem


@Composable
fun DialogRow(vector: ImageVector, content: @Composable RowScope.() -> Unit) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
        Icon(imageVector = vector, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        content()
    }
}

@Composable
fun NewProgramDialog(
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    showSnackbar: (String) -> Unit,
    viewModel: ProgramsViewModel
) {
    val scope = rememberCoroutineScope()
    var datePopup by remember { mutableStateOf<DatePopup?>(null) }
    val currentDate = Clock.System.now().let {
        val date = it.toLocalDateTime(TimeZone.currentSystemDefault())
        LocalDateTime(date.year, date.month, date.day, date.hour, 0, 0, 0)
    }.toInstant(TimeZone.currentSystemDefault())
    var startDate by remember { mutableStateOf(currentDate) }
    var endDate by remember { mutableStateOf(currentDate + 1.hours) }
    Box {
        Column {
            DialogContainer("Új program létrehozása", 350.dp, close, navigate) {
                Column {
                    var programTitle by remember { mutableStateOf("") }
                    var description by remember { mutableStateOf("") }
                    var mandatory by remember { mutableStateOf(false) }

                    TextField(
                        value = programTitle,
                        onValueChange = { programTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(imageVector = LucideUsers.lightIcon, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Kötelező?")
                            Checkbox(
                                checked = mandatory,
                                onCheckedChange = { mandatory = it }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    DialogRow(LucideClock4) {
                        Column {
                            DateTimeInput(
                                value = startDate,
                                target = DatePopupTarget.START,
                                onPickStart = { datePopup = it }
                            )
                            Spacer(Modifier.height(4.dp))
                            DateTimeInput(
                                value = endDate,
                                target = DatePopupTarget.END,
                                onPickStart = { datePopup = it }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    DialogRow(LucideSquarePen) {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            maxLines = Int.MAX_VALUE,
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    SubmitButton(
                        onClick = {
                            if (programTitle.isEmpty()) {
                                scope.launch {
                                    showSnackbar("A cím nem lehet üres!")
                                }
                                return@SubmitButton
                            }
                            if (startDate > endDate) {
                                scope.launch {
                                    showSnackbar("A program nem fejeződhet be hamarabb mint ahogy elkezdődne!")
                                }
                                return@SubmitButton
                            }

                            viewModel.createProgram(
                                programTitle,
                                description,
                                startDate.toEpochMilliseconds(),
                                endDate.toEpochMilliseconds(),
                                mandatory
                            ) { result ->
                                scope.launch {
                                    showSnackbar(
                                        when (result) {
                                            is DbResult.Success -> "Program létrehozva!"
                                            is DbResult.Failure -> "Hiba történt a program létrehozásakor: ${result.description}"
                                        }
                                    )
                                }
                                if (result is DbResult.Success) close()
                            }
                        },
                        label = "Program létrehozása",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (datePopup != null) {
            val default = when (datePopup!!) {
                is DatePopup.Date -> (if (datePopup!!.target == DatePopupTarget.START) startDate else endDate).toEpochMilliseconds()
                is DatePopup.Time -> {
                    val time = when (datePopup!!.target) {
                        DatePopupTarget.START -> startDate
                        DatePopupTarget.END -> endDate
                    }.toLocalDateTime(TimeZone.currentSystemDefault()).time
                    (time.hour * 60 + time.minute).toLong()
                }
            }

            DatePickerPopup(
                datePopup!!,
                default,
            ) {
                println(it)
                when (datePopup!!) {
                    is DatePopup.Date -> {
                        if (it != null) when (datePopup!!.target) {
                            DatePopupTarget.START -> {
                                val startTime = startDate.toLocalDateTime(TimeZone.UTC).time
                                startDate =
                                    Instant.fromEpochMilliseconds(it) + startTime.hour.hours + startTime.minute.minutes
                                println("${Instant.fromEpochMilliseconds(it)} ${startTime.hour} ${startTime.minute}")
                            }

                            DatePopupTarget.END -> {
                                val endTime =
                                    (endDate).toLocalDateTime(TimeZone.UTC).time
                                endDate =
                                    Instant.fromEpochMilliseconds(it) + endTime.hour.hours + endTime.minute.minutes
                                println("${Instant.fromEpochMilliseconds(it)} ${endTime.hour} ${endTime.minute}")
                            }
                        }
                    }

                    is DatePopup.Time -> {
                        if (it != null) when (datePopup!!.target) {
                            DatePopupTarget.START -> {
                                val startLocalDate =
                                    (startDate).toLocalDateTime(TimeZone.currentSystemDefault()).date
                                startDate =
                                    startLocalDate.atStartOfDayIn(TimeZone.currentSystemDefault()) + it.milliseconds
                            }

                            DatePopupTarget.END -> {
                                val endLocalDate = (endDate).toLocalDateTime(TimeZone.currentSystemDefault()).date
                                endDate = endLocalDate.atStartOfDayIn(TimeZone.currentSystemDefault()) + it.milliseconds
                            }
                        }
                    }
                }
                datePopup = null
            }
        }
    }
}