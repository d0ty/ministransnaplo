/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.models.Member
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.DialogContainer
import kotlinx.serialization.Serializable

@Serializable
data class MemberDetail(val member: Member) : NavItem

@Composable
fun MemberDetailDialog(
    member: Member,
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    viewModel: MembersViewModel = viewModel { MembersViewModel() }
) {
    DialogContainer("${member.name} adatlapja", 400.dp, close, navigate) {
        Text(member.id)
    }
}