/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.NavContainer
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.FullScreenCard
import hu.ministransnaplo.app.ui.components.ScreenTitleBar
import hu.ministransnaplo.app.ui.icons.lucide.*
import io.github.windedge.table.DataTable
import kotlinx.serialization.Serializable

@Serializable
object Members : NavItem

enum class MemberTableColumns(val id: String?, val title: String) {
    Name("name", "Név"),
    Rank("login", "Rang"),
    Justifications(null, "Igazolások"),
    Omissions(null, "Mulasztások")
}

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
                var queryValue by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = queryValue,
                    onValueChange = {
                        queryValue = it
                        viewModel.fetchMemberTable(
                            it,
                            tableState.order.column,
                            tableState.order.ascending
                        )
                    },
                    placeholder = { Text("Keresés") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(percent = 50),
                    leadingIcon = {
                        if (tableState.isLoading)
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        else Image(
                            imageVector = LucideSearch,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                )
                Spacer(Modifier.height(16.dp))
                DataTable(
                    modifier = Modifier.fillMaxSize(),
                    columns = {
                        MemberTableColumns.entries.forEach { columnData ->
                            column {
                                Row {
                                    val orderIcon = if (tableState.order.column == columnData) {
                                        if (tableState.order.ascending) LucideChevronUp else LucideChevronDown
                                    } else {
                                        LucideChevronsUpDown
                                    }
                                    Text(columnData.title)
                                    Spacer(Modifier.width(2.dp))
                                    Image(
                                        imageVector = orderIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp).clickable {
                                            if (tableState.order.ascending) viewModel.fetchMemberTable(
                                                "",
                                                columnData,
                                                false
                                            )
                                            else viewModel.fetchMemberTable("", columnData, true)
                                        })
                                }
                            }
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
