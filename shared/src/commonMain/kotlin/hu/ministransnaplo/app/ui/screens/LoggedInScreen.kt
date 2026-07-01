/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.NavContainer
import hu.ministransnaplo.app.models.Guard
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.icons.AppLogo
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object LoggedIn : NavItem

@Composable
fun LoggedInScreen(onNavigation: (NavItem) -> Unit, viewModel: AppViewModel = viewModel { AppViewModel() }) {
    val scope = rememberCoroutineScope()
    scope.launch {
        viewModel.supabase.from("guard").select().decodeAs<List<Guard>>().forEach {
            println("${it.id} ${it.name}")
        }
    }
    NavContainer(onNavigation = onNavigation) {
        Image(imageVector = AppLogo, contentDescription = "Logo", modifier = Modifier.fillMaxSize())
    }
}