package hu.ministransnaplo.app.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import hu.ministransnaplo.app.ui.icons.AppLogo
import kotlinx.serialization.Serializable

@Serializable
object LoggedIn

@Composable
fun LoggedInScreen() {
    Image(imageVector = AppLogo, contentDescription = "Logo")
}