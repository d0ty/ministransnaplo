/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hu.ministransnaplo.app.ui.NavItem


data class DialogCommand(
    val icon: ImageVector,
    val title: String,
    val action: () -> Unit
)

class DialogCommandRegistry() {
    val commands = arrayListOf<DialogCommand>()

    val isNotEmpty get() = commands.isNotEmpty()

    fun command(icon: ImageVector, title: String, action: () -> Unit) {
        commands.add(DialogCommand(icon, title, action))
    }
}

@Composable
expect fun DialogContainer(
    title: String,
    webWidth: Dp = 400.dp,
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    trailingIcon: @Composable () -> Unit = {},
    commands: DialogCommandRegistry.() -> Unit = {},
    commandsEnabled: Boolean = true,
    content: @Composable () -> Unit
)