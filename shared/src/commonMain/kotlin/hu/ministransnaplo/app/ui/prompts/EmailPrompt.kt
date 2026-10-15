/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.prompts

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.CardColumn
import hu.ministransnaplo.app.ui.icons.lucide.LucideX
import io.github.jan.supabase.compose.auth.ui.FormValidator
import kotlinx.serialization.Serializable

/**
 * ID of email prompting requests. Used in callback handling.
 * Extend if you find new places where you need to prompt an email address.
 *
 * @see EmailPrompt
 */
@Serializable
enum class EmailPromptRequests(
    val title: String,
) {
    PROMOTE_MEMBER("Tag előléptetése");
}

/**
 * NavType for the email prompter dialog.
 * Specify your request using [EmailPromptRequests] (if you find a new need, extend it freely)
 * and your description of why you need that address from your user.
 *
 * The prompt will return to the last back state entry with saved state entry "result" that is
 * either null (if the user cancelled) or [PromptResult.Email] with the request for receiver side identification
 * and the email address itself.
 * Your receiver side must add [PromptResultNavType] to the type map to be able to parse the result.
 *
 * @param request [EmailPromptRequests] the request type for the prompt
 * @param description [String] the description of why you need the email address from the user
 */
@Serializable
data class EmailPrompt(
    val request: EmailPromptRequests,
    val description: String,
) : NavItem

/**
 * Actual email prompt implementation. Should not be used outside the nav graph.
 *
 * @param config [EmailPrompt] navigation arguments to be displayed
 * @param finish a callback that is fired when the user finishes the prompting process
 * by either cancelling or submitting a valid email address. The callback will return either null (if the user cancelled)
 * or [PromptResult.Email] with the request and the email address.
 */
@Composable
fun EmailPromptDialog(
    config: EmailPrompt,
    finish: (PromptResult?) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }

    CardColumn(Modifier.width(IntrinsicSize.Min).padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(config.request.title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            Image(LucideX, "", Modifier.size(24.dp).clickable(onClick = { finish(null) }))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(config.description, fontSize = 16.sp)
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (!isValid) isValid = FormValidator.EMAIL.validate(it)
            },
            label = { Text("Email") },
            isError = !isValid,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                isValid = FormValidator.EMAIL.validate(email)
                if (isValid) finish(PromptResult.Email(config.request, email))
            }, modifier = Modifier.align(Alignment.End), enabled = isValid
        ) {
            Text("Submit")
        }
    }
}