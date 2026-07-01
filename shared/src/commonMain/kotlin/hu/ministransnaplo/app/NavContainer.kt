/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ministransnaplo.app.ui.NavItem

@Composable
expect fun NavContainer(
    viewModel: AppViewModel = viewModel { AppViewModel() },
    onNavigation: (NavItem) -> Unit,
    content: @Composable () -> Unit,
)