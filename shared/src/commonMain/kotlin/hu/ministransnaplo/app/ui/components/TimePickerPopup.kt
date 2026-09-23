/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun TimePickerPopup(onConfirm: (Long) -> Unit, onDismiss: () -> Unit)