/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui

import hu.ministransnaplo.app.ui.icons.IconVariants
import hu.ministransnaplo.app.ui.icons.lucide.LucideCalendarDays
import hu.ministransnaplo.app.ui.icons.lucide.LucideHouse
import hu.ministransnaplo.app.ui.icons.lucide.LucidePodium
import hu.ministransnaplo.app.ui.icons.lucide.LucideUsers
import hu.ministransnaplo.app.ui.screens.LoggedIn

data class NavSubmenuItem(val title: String, val destination: NavItem = LoggedIn)

interface NavMenuItem {
    val title: String
    val icon: IconVariants
    val destination: NavItem
    val submenuItems: Array<NavSubmenuItem>
}

enum class NavMenuItems(
    override val title: String,
    override val icon: IconVariants,
    override val destination: NavItem,
    override val submenuItems: Array<NavSubmenuItem> = emptyArray()
) : NavMenuItem {
    Home("Kezdőlap", LucideHouse, LoggedIn), // TODO: replace with actual destination
    Members("Tagok", LucideUsers, LoggedIn), // TODO: replace with actual destination

    // TODO: replace with actual destination
    Programs(
        "Programok", LucideCalendarDays, LoggedIn, arrayOf(
            NavSubmenuItem("Összesítők", LoggedIn)// TODO: replace with actual destination
        )
    ),
    Competition("Pontverseny", LucidePodium, LoggedIn)// TODO: replace with actual destination,
}