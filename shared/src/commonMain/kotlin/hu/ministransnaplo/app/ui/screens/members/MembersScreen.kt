/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.NavContainer
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.CardColumn
import hu.ministransnaplo.app.ui.components.ScreenTitleBar
import hu.ministransnaplo.app.ui.icons.lucide.LucideUserPlus
import kotlinx.serialization.Serializable

@Serializable
object Members : NavItem

@Composable
fun MembersScreen(onNavigation: (NavItem) -> Unit, viewModel: MembersViewModel = viewModel { MembersViewModel() }) {
    val userState by viewModel.userState.collectAsState()
    println("${userState.isGuardOwner} ${userState.guard?.ownerId} ${userState.userId}")
    NavContainer(onNavigation = onNavigation) {
        Column(Modifier.fillMaxSize()) {
            ScreenTitleBar("Tagok", LucideUserPlus, userState.isGuardOwner) {
                onNavigation(NewMember)
            }
            Spacer(Modifier.height(16.dp))
            CardColumn(Modifier.fillMaxSize().fillMaxWidth()) {
                // TODO: implement user listing
                Row(modifier = Modifier.fillMaxWidth()) {}
            }
        }
    }
}
