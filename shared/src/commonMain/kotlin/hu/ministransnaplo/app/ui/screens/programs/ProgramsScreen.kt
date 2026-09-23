/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hu.ministransnaplo.app.NavContainer
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.FullScreenCard
import hu.ministransnaplo.app.ui.components.ScreenTitleBar
import hu.ministransnaplo.app.ui.icons.lucide.LucideCalendarPlus
import kotlinx.serialization.Serializable

@Serializable
object Programs : NavItem

@Serializable
object ProgramsRoot : NavItem

@Composable
fun ProgramsScreen(onNavigation: (NavItem) -> Unit, viewModel: ProgramsViewModel) {
    val userState by viewModel.userState.collectAsState()
    NavContainer(onNavigation = onNavigation) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitleBar("Programok", LucideCalendarPlus, userState.isGuardOwner) {
                onNavigation(NewProgram)
            }
            Spacer(modifier = Modifier.height(16.dp))
            FullScreenCard(Modifier.fillMaxSize().fillMaxWidth()) {

            }
        }
    }
}