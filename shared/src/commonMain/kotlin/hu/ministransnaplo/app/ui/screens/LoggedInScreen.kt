/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hu.ministransnaplo.app.NavContainer
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.icons.AppLogo
import kotlinx.serialization.Serializable

@Serializable
object LoggedIn : NavItem

@Composable
fun LoggedInScreen(onNavigation: (NavItem) -> Unit) {
    NavContainer(onNavigation = onNavigation) {
        Image(imageVector = AppLogo, contentDescription = "Logo", modifier = Modifier.fillMaxSize())
    }
}