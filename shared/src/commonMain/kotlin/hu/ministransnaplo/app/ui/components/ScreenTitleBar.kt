/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

data class TitleBarStyle(val textStyle: TextStyle, val iconSize: Dp)

expect val titleBarStyle: TitleBarStyle

@Composable
fun ScreenTitleBar(title: String, icon: ImageVector, onNavigation: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = title, style = titleBarStyle.textStyle)
        Image(icon, "", modifier = Modifier.clickable(onClick = onNavigation).size(titleBarStyle.iconSize))
    }
}