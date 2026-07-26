/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.Theme
import hu.ministransnaplo.app.ui.icons.lucide.LucideX

@Composable
actual fun DialogContainer(
    title: String,
    webWidth: Dp,
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    trailingIcon: @Composable (() -> Unit),
    commands: DialogCommandRegistry.() -> Unit,
    content: @Composable () -> Unit
) {
    val commands = DialogCommandRegistry().apply(commands)
    Column(
        Modifier
            .clip(RoundedCornerShape(16f))
            .background(Theme.colorScheme.background)
            .padding(16.dp)
            .width(webWidth)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                trailingIcon()
                Image(LucideX, "", Modifier.size(24.dp).clickable(onClick = close))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}