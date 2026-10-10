/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.CardColumn
import hu.ministransnaplo.app.ui.components.DataCard
import hu.ministransnaplo.app.ui.components.DialogContainer
import hu.ministransnaplo.app.ui.components.FlexBox
import hu.ministransnaplo.app.ui.icons.lucide.LucideCircleCheckBig
import hu.ministransnaplo.app.ui.icons.lucide.LucideSquarePen
import kotlinx.serialization.Serializable

@Serializable
object ProgramDetails : NavItem

@Composable
fun ProgramDetailDialog(
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    viewModel: ProgramsViewModel
) {
    val program by viewModel.currentMember.collectAsState()
    var editing by remember { mutableStateOf(false) }

    DialogContainer("${program!!.title} adatai", 500.dp, close, navigate, commands = {
        command(LucideSquarePen, "Adatok szerkesztése") {
            editing = true
        }
    }, commandsEnabled = viewModel.userState.value.isGuardOwner) {
        FlexBox(modifier = Modifier.fillMaxWidth()) {
            DataCard(Modifier.fillMaxFlexSpace(), editing, onEditFinishes = {
                editing = false
            }) {
                TextField("title", "Cím", program!!.title)
                DateRangeField("time", "Dátum", program!!.startDate, program!!.duration)
                TextField("type", "Típus", "Program", true)
                CheckboxField("mandatory", "Kötelező", program!!.mandatory)
            }
            FlexibleSpacer(16.dp)
            CardColumn(Modifier.fillMaxFlexSpace(), horizontalAlignment = Alignment.Start) {
                Text("Jelenlét igazolása", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().defaultMinSize(200.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 2.dp)
                    ) {
                        Text("User 1", fontSize = 12.sp)
                        Image(LucideCircleCheckBig, null, Modifier.size(16.dp).clickable {
                            // TODO: implement with the future justification system
                        })
                    }
                }
            }
        }
    }
}