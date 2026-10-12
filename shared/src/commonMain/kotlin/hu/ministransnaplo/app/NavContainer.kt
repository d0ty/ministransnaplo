/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app

import androidx.compose.runtime.Composable
import hu.ministransnaplo.app.ui.NavItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun NavContainer(
    viewModel: AppViewModel = koinViewModel(),
    onNavigation: (NavItem) -> Unit,
    content: @Composable () -> Unit,
)