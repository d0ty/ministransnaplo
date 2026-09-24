/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.programs

import androidx.compose.runtime.Composable
import hu.ministransnaplo.app.ui.NavItem

@Composable
actual fun WeeklyProgramsView(
    viewData: ProgramsViewModel.ViewData,
    onNavigation: (NavItem) -> Unit
) {
}

actual fun getDaysOfWeek(): Array<String> =
    arrayOf("Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek", "Szombat", "Vasárnap")