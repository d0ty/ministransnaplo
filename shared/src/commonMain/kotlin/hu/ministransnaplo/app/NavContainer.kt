/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app

import androidx.compose.runtime.Composable
import hu.ministransnaplo.app.ui.NavItem

@Composable
expect fun NavContainer(onNavigation: (NavItem) -> Unit, content: @Composable () -> Unit)