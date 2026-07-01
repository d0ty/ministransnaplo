/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.models.Guard
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.icons.AppLogo
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object LoggedIn : NavItem

@Composable
fun LoggedInScreen(viewModel: AppViewModel = viewModel { AppViewModel() }) {
    val scope = rememberCoroutineScope()
    scope.launch {
        viewModel.supabase.from("guard").select().decodeAs<List<Guard>>().forEach {
            println("${it.id} ${it.name}")
        }
    }
    Image(imageVector = AppLogo, contentDescription = "Logo")
}