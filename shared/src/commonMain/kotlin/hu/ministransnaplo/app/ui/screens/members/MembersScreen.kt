/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.NavContainer
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.FullScreenCard
import hu.ministransnaplo.app.ui.components.ScreenTitleBar
import hu.ministransnaplo.app.ui.icons.lucide.LucideArrowRight
import hu.ministransnaplo.app.ui.icons.lucide.LucideUserPlus
import io.github.windedge.table.DataTable
import kotlinx.serialization.Serializable

@Serializable
object Members : NavItem

@Composable
fun MembersScreen(onNavigation: (NavItem) -> Unit, viewModel: MembersViewModel = viewModel { MembersViewModel() }) {
    val userState by viewModel.userState.collectAsState()
    val tableState by viewModel.tableState.collectAsState()
    NavContainer(onNavigation = onNavigation) {
        Column(Modifier.fillMaxSize()) {
            ScreenTitleBar("Tagok", LucideUserPlus, userState.isGuardOwner) {
                onNavigation(NewMember)
            }
            Spacer(Modifier.height(16.dp))
            FullScreenCard(Modifier.fillMaxSize().fillMaxWidth()) {
                DataTable(
                    modifier = Modifier.fillMaxSize(),
                    columns = {
                        column {
                            Text("Név")
                        }
                        column {
                            Text("Rang")
                        }
                        column {
                            Text("Igazolások")
                        }
                        column {
                            Text("Mulasztások")
                        }
                        column {}
                    }
                ) {
                    tableState.members.forEach { member ->
                        row {
                            cell { Text(member.name) }
                            cell { Text(member.rank) }
                            cell { Text("0") }
                            cell { Text("0") }
                            cell { Image(imageVector = LucideArrowRight.lightIcon, contentDescription = null) }
                        }
                    }
                }
            }
        }
    }
}
