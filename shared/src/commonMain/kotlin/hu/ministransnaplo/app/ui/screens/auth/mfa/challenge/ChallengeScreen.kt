/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.auth.mfa.challenge

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.SingleCardScreen
import hu.ministransnaplo.app.ui.components.SubmitButton
import kotlinx.serialization.Serializable

@Serializable
object MFAChallenge : NavItem {}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChallengeScreen(onSucess: () -> Unit, viewModel: MFAChallengeViewModel = viewModel { MFAChallengeViewModel() }) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    var mfaCode by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    SingleCardScreen(snackbarHostState = snackbarHostState) {
        state.value.guard?.ProfilePicture(viewModel.supabase, 128.dp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Üdvözlet ${state.value.displayName}! \n Kérlek add meg a hitelesítő alkalmazás" +
                    " által generált kódot a belépéshez.",
            fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = mfaCode, onValueChange = { mfaCode = it }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        SubmitButton(label = "Belépés", modifier = Modifier.fillMaxWidth(), onClick = {
            viewModel.verifyChallenge(mfaCode) { success ->
                if (!success) {
                    snackbarHostState.showSnackbar("2. faktor hitelesítése sikertelen. Kérjük próbáld újra!")
                    return@verifyChallenge
                }
                onSucess()
            }
        })
    }
}
