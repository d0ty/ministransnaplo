/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

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
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.components.CardColumn
import hu.ministransnaplo.app.ui.components.DataCard
import hu.ministransnaplo.app.ui.components.DialogContainer
import hu.ministransnaplo.app.ui.components.FlexBox
import hu.ministransnaplo.app.ui.icons.lucide.*
import hu.ministransnaplo.app.ui.prompts.DestructiveActionPrompt
import hu.ministransnaplo.app.ui.prompts.DestructivePromptRequests
import hu.ministransnaplo.app.ui.prompts.EmailPrompt
import hu.ministransnaplo.app.ui.prompts.EmailPromptRequests
import hu.ministransnaplo.app.util.DbResult
import kotlinx.serialization.Serializable

@Serializable
object MemberDetail : NavItem

@Composable
fun MemberDetailDialog(
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    viewModel: MembersViewModel = viewModel { MembersViewModel() }
) {
    var editing by remember { mutableStateOf(false) }
    val member by viewModel.currentMember.collectAsState()
    LaunchedEffect("key") { viewModel.currentMember.collect { println(it?.rank) } }

    DialogContainer("${member!!.name} adatlapja", 500.dp, close, navigate, commands = {
        command(LucideSquarePen, "Adatok szerkesztése") {
            editing = true
        }
        if (!member!!.isLeader) command(LucideUserStar, "Előléptetés vezetővé") {
            navigate(
                EmailPrompt(
                    EmailPromptRequests.PROMOTE_MEMBER,
                    "Kérünk add meg a tag e-mail címét, amivel be fog tudni jelentkezni!"
                )
            )
        }
        command(LucideOctagonX, "Tag törlése") {
            navigate(
                DestructiveActionPrompt(
                    DestructivePromptRequests.DELETE_MEMBER,
                    "Biztosan törölni szeretnéd ${member!!.name}-t ?",
                    "A tag törlése végleges, és minden adatát elveszíted."
                )
            )
        }
        if (member!!.isLeader) command(LucideUserRoundKey, "2. faktor visszaállítása") {
            navigate(
                DestructiveActionPrompt(
                    DestructivePromptRequests.RESET_MEMBER_MFA,
                    "Biztosan visszaállítod ${member!!.name} 2FA kulcsait?",
                    "A második faktor visszaállítása után ${member!!.name}-nek új kulcsokat kell beállítani magának." +
                            "Csak akkor állítsd vissza a kulcsokat, ha ${member!!.name} elvesztette a kulcsait" +
                            " vagy nem tud bejelentkezni a fiókjába."
                )
            )
        }
    }) {
        FlexBox(modifier = Modifier.fillMaxWidth()) {
            DataCard(modifier = Modifier.fillMaxFlexSpace(), editing = editing, onEditFinishes = { data ->
                if (data == null) {
                    editing = false
                    return@DataCard
                }
                viewModel.updateMember(data) {
                    when (it) {
                        is DbResult.Success -> {
                            editing = false
                        }

                        is DbResult.Failure -> {
                            editing = true
                        }
                    }
                }
            }) {
                TextField("name", "Név", member!!.name)
                TextField("rank", "Rang", member!!.rank, readOnly = true)
                if (editing) CheckboxField("islecturer", "Lektor", member!!.isLecturer)
                if (member!!.isLeader) TextField(
                    "email",
                    "E-mail cím",
                    member!!.email ?: "Nincs megadva",
                    readOnly = true
                ) // TODO: implementation of some sort
                TextField(
                    "sanctions",
                    "Aktív szankciók",
                    "nincsenek szankciók",
                    readOnly = true
                ) // TODO: implement with the future sanction system
            }
            FlexibleSpacer(16.dp)
            CardColumn(
                Modifier.fillMaxFlexSpace(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Igazolatlan programok", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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