/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector

interface IconVariants {
    val darkIcon: ImageVector
    val lightIcon: ImageVector
}

fun ImageVector.iconVariants(): IconVariants {
    val icon = this
    return object : IconVariants {
        override val darkIcon: ImageVector = icon
        override val lightIcon: ImageVector = icon
    }
}