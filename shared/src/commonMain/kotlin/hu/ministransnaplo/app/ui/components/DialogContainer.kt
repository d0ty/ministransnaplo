/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hu.ministransnaplo.app.ui.NavItem

@Composable
expect fun DialogContainer(
    title: String,
    webWidth: Dp = 400.dp,
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    trailingIcon: @Composable () -> Unit = {},
    content: @Composable () -> Unit
)