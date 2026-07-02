/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hu.ministransnaplo.app.NavContainer
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.icons.lucide.LucideArrowLeft

@Composable
actual fun DialogContainer(
    title: String,
    webWidth: Dp,
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    trailingIcon: @Composable (() -> Unit),
    content: @Composable () -> Unit
) {
    NavContainer(onNavigation = navigate) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(LucideArrowLeft, "", Modifier.size(titleBarStyle.iconSize).clickable(onClick = close))
                    Spacer(Modifier.width(8.dp))
                    Text(text = title, style = titleBarStyle.textStyle)
                }
                trailingIcon()
            }
            Spacer(Modifier.height(48.dp))
            content()
        }
    }
}