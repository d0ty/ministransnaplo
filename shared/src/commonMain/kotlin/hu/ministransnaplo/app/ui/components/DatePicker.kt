/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import hu.ministransnaplo.app.util.convertMillisToDateString
import hu.ministransnaplo.app.util.convertMillisToTimeString
import kotlin.time.Instant

sealed interface DatePopup {
    val target: DatePopupTarget

    data class Date(override val target: DatePopupTarget) : DatePopup
    data class Time(override val target: DatePopupTarget) : DatePopup
}

enum class DatePopupTarget {
    START, END
}

@Composable
fun DateTimeInput(value: Instant, target: DatePopupTarget, onPickStart: (DatePopup) -> Unit) {
    val dateMillis = value.toEpochMilliseconds()

    Row {
        TextField(
            value = convertMillisToDateString(dateMillis),
            onValueChange = { },
            placeholder = { Text("YYYY. MM. DD") },
            modifier = Modifier.sizeIn(maxWidth = 192.dp).pointerInput(dateMillis) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) onPickStart(DatePopup.Date(target))
                }
            }
        )
        Spacer(Modifier.width(8.dp))
        TextField(
            value = convertMillisToTimeString(dateMillis),
            onValueChange = { },
            placeholder = { Text("HH:mm") },
            modifier = Modifier.sizeIn(minWidth = 200.dp).pointerInput(dateMillis) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) onPickStart(DatePopup.Time(target))
                }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DatePicker(default: Long? = null, onPickFinished: (Long?) -> Unit) {
    val datePickerState = rememberDatePickerState(default)

    DatePickerDialog(
        onDismissRequest = { onPickFinished(null) },
        confirmButton = {
            TextButton(
                onClick = { onPickFinished(datePickerState.selectedDateMillis) }
            ) {
                Text("OK", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onPickFinished(null) }
            ) {
                Text("Mégse", color = MaterialTheme.colorScheme.primary)
            }
        }
    ) {
        DatePicker(datePickerState)
    }
}

@Composable
fun DatePickerPopup(popupData: DatePopup, defaultValueInMillis: Long?, onPickFinished: (Long?) -> Unit) {
    when (popupData) {
        is DatePopup.Date -> DatePicker(defaultValueInMillis, onPickFinished)
        is DatePopup.Time -> Dialog(onDismissRequest = {
            onPickFinished(null)
        }) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(Modifier.padding(10.dp)) {
                    TimePickerPopup(
                        default = defaultValueInMillis,
                        onConfirm = { onPickFinished(it) },
                        onDismiss = { onPickFinished(null) })
                }
            }
        }
    }
}