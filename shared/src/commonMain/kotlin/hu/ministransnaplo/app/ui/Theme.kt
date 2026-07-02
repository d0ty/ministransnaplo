/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

object Theme {
    val colorScheme = darkColorScheme(
        primary = Color(0xFF4878D7),
        background = Color(0xFF272534),
        surface = Color(0xFF434F60),
        surfaceVariant = Color(0xFFADAFBD),
        scrim = Color(0x80000000)
    )
}