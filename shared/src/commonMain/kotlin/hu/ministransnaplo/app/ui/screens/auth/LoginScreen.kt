/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.ui.components.CardColumn
import hu.ministransnaplo.app.ui.icons.AppLogo
import io.github.jan.supabase.compose.auth.ui.AuthForm
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object Login

@Composable
@OptIn(AuthUiExperimental::class, ExperimentalMaterial3Api::class)
fun LoginScreen(onLoggedIn: () -> Unit, viewModel: AppViewModel = viewModel { AppViewModel() }) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CardColumn {
                Image(AppLogo, contentDescription = null, Modifier.size(96.dp))
                Text("Ministráns Napló", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                AuthForm {
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    Column {
                        OutlinedEmailField(
                            email, onValueChange = { email = it },
                            textStyle = TextStyle(fontSize = 14.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.LightGray,
                                errorTextColor = Color.Red
                            ),
                            label = { Text(text = "Email", fontSize = 14.sp) }, mandatory = true
                        )
                        OutlinedPasswordField(
                            password, onValueChange = { password = it },
                            textStyle = TextStyle(fontSize = 14.sp),
                            label = { Text(text = "Jelszó", fontSize = 14.sp) }, mandatory = true
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.login(email, password) { success, reason ->
                                    if (success) onLoggedIn()
                                    else coroutineScope.launch {
                                        snackbarHostState.showSnackbar(reason!!)
                                    }
                                }
                            },
                            colors = ButtonColors(
                                containerColor = Color(0xFF33373E),
                                contentColor = Color.White,
                                disabledContainerColor = Color.White,
                                disabledContentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Bejelentkezés", textAlign = TextAlign.Center, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}