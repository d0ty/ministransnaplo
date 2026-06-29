/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */


package hu.ministransnaplo.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.ui.Theme
import hu.ministransnaplo.app.ui.icons.AppLogo
import io.github.jan.supabase.compose.auth.ui.AuthForm
import io.github.jan.supabase.compose.auth.ui.LocalAuthState
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField

@OptIn(AuthUiExperimental::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(viewModel: AppViewModel = viewModel { AppViewModel() }) {
    MaterialTheme(colorScheme = Theme.colorScheme) {
        Surface(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            contentColor = Color.White,
            color = Theme.colorScheme.background
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(16f))
                        .background(Theme.colorScheme.surface)
                        .padding(16.dp).width(IntrinsicSize.Max),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(AppLogo, contentDescription = null, Modifier.size(96.dp))
                    Text("Ministráns Napló", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    AuthForm {
                        var email by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }
                        val state = LocalAuthState.current
                        Column {
                            OutlinedEmailField(
                                email, onValueChange = { email = it },
                                textStyle = TextStyle(fontSize = 14.sp),
                                modifier = Modifier.height(50.dp),
                                label = { Text(text = "Email", fontSize = 14.sp) }, mandatory = true
                            )
                            OutlinedPasswordField(
                                password, onValueChange = { password = it },
                                textStyle = TextStyle(fontSize = 14.sp),
                                modifier = Modifier.height(50.dp),
                                label = { Text(text = "Jelszó", fontSize = 14.sp) }, mandatory = true
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {},
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
}