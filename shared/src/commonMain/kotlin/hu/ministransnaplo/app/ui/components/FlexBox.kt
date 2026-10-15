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
 * Configuration storage for `FlexBox`
 *
 * Subcomponents and extension methods are all have access
 * to the same configuration as the parent `FlexBox`
 */
class FlexBoxScope(val negateMobile: Boolean = false) {
    val isMobile get() = getPlatform().isMobile != negateMobile

    @Composable
    fun FlexibleSpacer(size: Dp) {
        if (isMobile) {
            Spacer(modifier = Modifier.height(size))
        } else {
            Spacer(modifier = Modifier.width(size))
        }
    }

    fun Modifier.fillMaxFlexSpace() = if (isMobile) Modifier.fillMaxWidth() else Modifier
}

/**
 * A platform dependent layout that has two mode:
 *  - on mobile it's a Column
 *  - on desktop it's a Row
 *
 *  The platform depends on `Platform.isMobile`, but it can be negatable using `negateMobile`.
 */
@Composable
fun FlexBox(
    negateMobile: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable FlexBoxScope.() -> Unit
) {
    val scope = FlexBoxScope(negateMobile)
    if (scope.isMobile) {
        Column(modifier) { scope.content() }
    } else {
        Row(modifier) { scope.content() }
    }
}