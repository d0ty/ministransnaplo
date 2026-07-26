/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import hu.ministransnaplo.app.getPlatform

/**
 * A platform dependent layout that has two mode:
 *  - on mobile it's a Column
 *  - on desktop it's a Row
 *
 *  The platform depends on `Platform.isMobile`.
 */
@Composable
fun FlexBox(negateMobile: Boolean = false, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    if (getPlatform().isMobile != negateMobile) {
        Column(modifier) { content() }
    } else {
        Row(modifier) { content() }
    }
}

@Composable
fun FlexibleSpacer(size: Dp) {
    if (getPlatform().isMobile) {
        Spacer(modifier = Modifier.height(size))
    } else {
        Spacer(modifier = Modifier.width(size))
    }
}