/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import hu.ministransnaplo.app.getPlatform

/**
 * A platform dependent layout that has two mode:
 *  - on mobile it's a Column
 *  - on desktop it's a Row
 *
 *  The platform depends on `Platform.isMobile`.
 */
@Composable
fun FlexBox(content: @Composable () -> Unit) {
    if (getPlatform().isMobile) {
        Column { content() }
    } else {
        Row { content() }
    }
}