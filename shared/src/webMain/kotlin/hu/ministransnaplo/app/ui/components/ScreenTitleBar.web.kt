/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

actual val titleBarStyle: TitleBarStyle
    get() = TitleBarStyle(
        textStyle = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.SemiBold),
        iconSize = 50.dp
    )