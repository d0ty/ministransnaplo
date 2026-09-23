/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.DialogContainer
import hu.ministransnaplo.app.ui.components.SubmitButton
import hu.ministransnaplo.app.ui.components.TimePickerPopup
import hu.ministransnaplo.app.ui.icons.lucide.LucideClock4
import hu.ministransnaplo.app.ui.icons.lucide.LucideSquarePen
import hu.ministransnaplo.app.ui.icons.lucide.LucideUsers
import hu.ministransnaplo.app.util.DbResult
import hu.ministransnaplo.app.util.convertMillisToDateString
import hu.ministransnaplo.app.util.convertMillisToTimeString
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object NewProgram : NavItem

sealed interface DatePopup {
    val target: DatePopupTarget

    data class Date(override val target: DatePopupTarget) : DatePopup
    data class Time(override val target: DatePopupTarget) : DatePopup
}

enum class DatePopupTarget {
    START, END
}

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
    var startDate by remember { mutableStateOf<Long?>(null) }
    var startTime by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var endTime by remember { mutableStateOf<Long?>(null) }
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
                            Row {
                                TextField(
                                    value = startDate?.let { convertMillisToDateString(it) } ?: "",
                                    onValueChange = { },
                                    placeholder = { Text("YYYY. MM. DD") },
                                    modifier = Modifier.sizeIn(maxWidth = 192.dp).pointerInput(startDate) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                datePopup = DatePopup.Date(DatePopupTarget.START)
                                            }
                                        }
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                TextField(
                                    value = startTime?.let { convertMillisToTimeString(it) } ?: "",
                                    onValueChange = { },
                                    placeholder = { Text("HH:mm") },
                                    modifier = Modifier.sizeIn(minWidth = 200.dp).pointerInput(startTime) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                datePopup = DatePopup.Time(DatePopupTarget.START)
                                            }
                                        }
                                    }
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Row {
                                TextField(
                                    value = endDate?.let { convertMillisToDateString(it) } ?: "",
                                    onValueChange = { },
                                    placeholder = { Text("YYYY. MM. DD") },
                                    modifier = Modifier.sizeIn(maxWidth = 192.dp).pointerInput(endDate) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                datePopup = DatePopup.Date(DatePopupTarget.END)
                                            }
                                        }
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                TextField(
                                    value = endTime?.let { convertMillisToTimeString(it) } ?: "",
                                    onValueChange = { },
                                    placeholder = { Text("HH:mm") },
                                    modifier = Modifier.fillMaxWidth().pointerInput(endTime) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                datePopup = DatePopup.Time(DatePopupTarget.END)
                                            }
                                        }
                                    }
                                )
                            }
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
                            if (startDate == null && startTime == null) {
                                scope.launch {
                                    showSnackbar("A kezdés dátuma nem lehet üres!")
                                }
                                return@SubmitButton
                            }
                            if (endDate == null && endTime == null) {
                                scope.launch {
                                    showSnackbar("A vége dátuma nem lehet üres!")
                                }
                                return@SubmitButton
                            }

                            if (startDate!! < endDate!!) {
                                scope.launch {
                                    showSnackbar("A program nem fejeződhet be hamarabb mint ahogy elkezdődne!")
                                }
                                return@SubmitButton
                            }

                            viewModel.createProgram(
                                programTitle,
                                description,
                                startDate!! + startTime!!,
                                endDate!! + endTime!!,
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
        when (datePopup) {
            is DatePopup.Date -> {
                val datePickerState = rememberDatePickerState()

                @OptIn(ExperimentalMaterial3Api::class)
                DatePickerDialog(
                    onDismissRequest = { datePopup = null },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                when (datePopup!!.target) {
                                    DatePopupTarget.START -> {
                                        startDate = datePickerState.selectedDateMillis
                                        if (endDate == null) endDate = datePickerState.selectedDateMillis
                                    }

                                    DatePopupTarget.END -> {
                                        endDate = datePickerState.selectedDateMillis
                                    }
                                }
                                datePopup = null
                            }
                        ) {
                            Text("OK", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { datePopup = null }
                        ) {
                            Text("Mégse", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                ) {
                    DatePicker(datePickerState, modifier = Modifier.sizeIn(maxWidth = 300.dp, maxHeight = 400.dp))
                }
            }

            is DatePopup.Time -> {
                Surface(
                    Modifier.align(Alignment.Center),
                    RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(Modifier.padding(10.dp)) {
                        TimePickerPopup(onConfirm = {
                            when (datePopup!!.target) {
                                DatePopupTarget.START -> {
                                    startTime = it
                                    if (endTime == null) endTime = it + 3600 * 1000
                                }

                                DatePopupTarget.END -> {
                                    endTime = it
                                }
                            }
                            datePopup = null
                        }, onDismiss = { datePopup = null })
                    }
                }
            }

            else -> {}
        }
    }
}