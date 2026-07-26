/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.models.Member
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.CardColumn
import hu.ministransnaplo.app.ui.components.DialogContainer
import hu.ministransnaplo.app.ui.components.FlexBox
import hu.ministransnaplo.app.ui.components.FlexibleSpacer
import hu.ministransnaplo.app.ui.icons.lucide.LucideCircleCheckBig
import hu.ministransnaplo.app.ui.icons.lucide.LucideRotateCcw
import kotlinx.serialization.Serializable

@Serializable
data class MemberDetail(val member: Member) : NavItem

@Composable
fun DataField(field: String, value: String) {
    FlexBox(negateMobile = true) {
        Text("$field:", fontWeight = FontWeight.SemiBold)
        FlexibleSpacer(2.dp)
        Text(value)
    }
}

@Composable
fun MemberDetailDialog(
    member: Member,
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    viewModel: MembersViewModel = viewModel { MembersViewModel() }
) {
    DialogContainer("${member.name} adatlapja", 500.dp, close, navigate) {
        FlexBox(modifier = Modifier.width(IntrinsicSize.Max).fillMaxWidth()) {
            CardColumn(horizontalAlignment = Alignment.Start) {
                Text("Adatok", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                DataField("Név", member.name)
                DataField("Rang", member.rank)
                if (member.isLeader) DataField("E-mail cím", member.email ?: "Nincs megadva")
                DataField("Aktív szankciók", "nincsenek szankciók") // TODO: implement with the future sanction system
            }
            FlexibleSpacer(16.dp)
            CardColumn(
                Modifier.height(IntrinsicSize.Max).width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Igazolatlan programok")
                    Image(LucideRotateCcw, null, Modifier.size(20.dp).clickable {
                        // TODO: implement with the future program system
                    })
                }
                Spacer(Modifier.height(12.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().defaultMinSize(250.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 2.dp)
                    ) {
                        Text("@MM.DD. HH:MM Program neve", fontSize = 12.sp)
                        Image(LucideCircleCheckBig, null, Modifier.size(16.dp).clickable {
                            // TODO: implement with the future program system
                        })
                    }
                }
            }
        }

    }
}