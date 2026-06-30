/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.ui.screens.auth.mfa

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.svg.SvgDecoder
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.Theme
import hu.ministransnaplo.app.ui.components.SingleCardScreen
import hu.ministransnaplo.app.ui.components.SubmitButton
import hu.ministransnaplo.app.ui.icons.lucide.LucideClipboard
import hu.ministransnaplo.app.util.toClipEntry
import io.ktor.utils.io.core.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object MFAEnroll : NavItem {}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EnrollScreen(onSucess: () -> Unit, viewModel: MFAViewModel = viewModel { MFAViewModel() }) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val imageLoader = ImageLoader.Builder(LocalPlatformContext.current)
        .components {
            add(SvgDecoder.Factory())
        }.build()
    val clipboard = LocalClipboard.current
    var mfaCode by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    SingleCardScreen(snackbarHostState = snackbarHostState) {
        Text("2. faktor hozzáadása", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(
            "Olvasd be a QR kódot egy hitelesítő alkalmazásba vagy másold be a lenti kódot. \n" +
                    "Ezután add meg az alkalmazás által generált kódot.",
            fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(200.dp).clip(RoundedCornerShape(16.dp)).background(color = Color.White).padding(4.dp)
            ) {
                if (!state.value.loading) AsyncImage(
                    model = state.value.qrCode.toByteArray(),
                    contentDescription = "",
                    imageLoader = imageLoader,
                    modifier = Modifier.width(200.dp).align(Alignment.Center)
                ) else CircularProgressIndicator(
                    Modifier.align(Alignment.Center),
                    color = Theme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.height(IntrinsicSize.Min)) {
            Text(state.value.secret, modifier = Modifier.align(Alignment.CenterVertically))
            Image(LucideClipboard, contentDescription = "", modifier = Modifier.clickable {
                viewModel.viewModelScope.launch {
                    clipboard.setClipEntry(state.value.secret.toClipEntry())
                }
            })
        }
        OutlinedTextField(value = mfaCode, onValueChange = { mfaCode = it }, modifier = Modifier.fillMaxWidth())
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