/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.SingleCardScreen
import hu.ministransnaplo.app.ui.components.SubmitButton
import hu.ministransnaplo.app.ui.icons.AppLogo
import io.github.jan.supabase.compose.auth.ui.AuthForm
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object Login : NavItem

@Composable
@OptIn(AuthUiExperimental::class, ExperimentalMaterial3Api::class)
fun LoginScreen(onLoggedIn: (NavItem) -> Unit, viewModel: LoginViewModel = viewModel { LoginViewModel() }) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    SingleCardScreen(snackbarHostState = snackbarHostState) {
        Image(AppLogo, contentDescription = null, Modifier.size(96.dp))
        Text("Ministráns Napló", fontWeight = FontWeight.Bold, fontSize = 28.sp)
        AuthForm {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            Column {
                OutlinedEmailField(
                    email, onValueChange = { email = it },
                    textStyle = TextStyle(fontSize = 14.sp),
                    label = { Text(text = "Email", fontSize = 14.sp) }, mandatory = true
                )
                OutlinedPasswordField(
                    password, onValueChange = { password = it },
                    textStyle = TextStyle(fontSize = 14.sp),
                    label = { Text(text = "Jelszó", fontSize = 14.sp) }, mandatory = true
                )
                Spacer(Modifier.height(8.dp))
                SubmitButton(
                    onClick = {
                        viewModel.login(email, password) {
                            if (it.success && it.navItem != null) onLoggedIn(it.navItem)
                            else if (it.errorMessage != null) coroutineScope.launch {
                                snackbarHostState.showSnackbar(it.errorMessage)
                            }
                        }
                    },
                    label = "Bejelentkezés",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}