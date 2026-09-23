/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TimePickerPopup(onConfirm: (Long) -> Unit, onDismiss: () -> Unit) {
    val timePickerState = rememberTimePickerState(
        is24Hour = true
    )

    Column {
        TimePicker(timePickerState)
        Row(horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { onConfirm(timePickerState.hour * 3600L * 1000 + timePickerState.minute * 60L * 1000) }) {
                Text("OK")
            }
            TextButton(onClick = { onDismiss() }) {
                Text("Mégse")
            }
        }
    }
}