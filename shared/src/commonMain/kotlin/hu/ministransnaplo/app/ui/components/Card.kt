/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import hu.ministransnaplo.app.ui.Theme

@Composable
fun CardColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(16f))
            .background(Theme.colorScheme.surface)
            .padding(16.dp).width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
fun SingleCardScreen(snackbarHostState: SnackbarHostState, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CardColumn {
                content()
            }
        }
    }
}