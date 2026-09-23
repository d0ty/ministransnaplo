/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TimePickerPopup(onConfirm: (Long) -> Unit, onDismiss: () -> Unit) {
    val state = rememberTimePickerState(is24Hour = true)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TimeInput(state = state)
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.sizeIn(maxWidth = 200.dp).fillMaxWidth()
        ) {
            TextButton(onClick = { onConfirm(state.hour * 3600L * 1000 + state.minute * 60L * 1000) }) {
                Text("OK")
            }
            TextButton(onClick = { onDismiss() }) {
                Text("Mégse")
            }
        }
    }
}