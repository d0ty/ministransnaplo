/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.DialogContainer
import hu.ministransnaplo.app.ui.components.SubmitButton
import hu.ministransnaplo.app.util.DbResult
import io.github.jan.supabase.compose.auth.ui.FormValidator
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object NewMember : NavItem

@Composable
fun NewMemberDialog(
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    showSnackbar: (String) -> Unit,
    viewModel: MembersViewModel = viewModel { MembersViewModel() }
) {
    val scope = rememberCoroutineScope()
    Column {
        DialogContainer("Új tag hozzáadása", 300.dp, close, navigate) {
            Column {
                var memberName by remember { mutableStateOf("") }
                var isLecturer by remember { mutableStateOf(false) }
                var email by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = memberName,
                    onValueChange = { memberName = it },
                    label = { Text("Név") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lektor?")
                    Checkbox(
                        checked = isLecturer,
                        onCheckedChange = { isLecturer = it }
                    )
                }
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (csak ha vezetőt hozol létre)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                SubmitButton(
                    onClick = {
                        if (memberName.isEmpty()) {
                            scope.launch {
                                showSnackbar("A név mező nem lehet üres!")
                                close()
                            }
                            return@SubmitButton
                        }
                        if (email.isNotEmpty() && !FormValidator.EMAIL.validate(email)) {
                            scope.launch {
                                showSnackbar("Az email cím nem érvényes!")
                                close()
                            }
                            return@SubmitButton
                        }
                        viewModel.createMember(memberName, isLecturer, email) { result ->
                            scope.launch {
                                showSnackbar(
                                    when (result) {
                                        is DbResult.Success -> "Sikeres tagfelvétel!"
                                        is DbResult.Failure -> "Hiba történt a tagfelvétel során: ${result.description}"
                                    }
                                )
                                close()
                            }
                        }
                    },
                    label = "Tag felvétele",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}