/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */


package hu.ministransnaplo.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    ComposeViewport {
        App()
    }
}