/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.icons.AppLogo
import kotlinx.serialization.Serializable

@Serializable
object LoggedIn : NavItem

@Composable
fun LoggedInScreen() {
    Image(imageVector = AppLogo, contentDescription = "Logo")
}