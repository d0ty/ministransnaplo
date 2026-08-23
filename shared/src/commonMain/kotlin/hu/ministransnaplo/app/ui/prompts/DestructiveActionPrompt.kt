/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.prompts

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import hu.ministransnaplo.app.ui.NavItem
import kotlinx.serialization.Serializable

@Serializable
enum class DestructivePromptRequests {
    DELETE_MEMBER,
}

@Serializable
data class DestructiveActionPrompt(
    val request: DestructivePromptRequests,
    val title: String,
    val description: String,
) : NavItem

@Composable
fun DestructiveActionPromptDialog(config: DestructiveActionPrompt, finish: (PromptResult.DestructiveAction) -> Unit) {
    AlertDialog(
        onDismissRequest = { finish(PromptResult.DestructiveAction(config.request, false)) },
        title = { Text(text = config.title) },
        text = { Text(text = config.description) },
        confirmButton = {
            TextButton(onClick = { finish(PromptResult.DestructiveAction(config.request, true)) }) {
                Text("Tovább", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = { finish(PromptResult.DestructiveAction(config.request, false)) }) {
                Text("Mégse")
            }
        }
    )
}