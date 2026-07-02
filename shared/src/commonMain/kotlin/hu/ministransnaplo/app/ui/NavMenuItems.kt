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
import hu.ministransnaplo.app.ui.screens.members.Members as MembersDest

data class NavSubmenuItem(val title: String, val destination: NavItem = LoggedIn)

interface NavMenuItem {
    val title: String
    val icon: IconVariants
    val destination: NavItem
    val submenuItems: ArrayList<NavSubmenuItem>
}

enum class NavMenuItems(
    override val title: String,
    override val icon: IconVariants,
    override val destination: NavItem,
    override val submenuItems: ArrayList<NavSubmenuItem> = arrayListOf(),
) : NavMenuItem {
    Home("Kezdőlap", LucideHouse, LoggedIn), // TODO: replace with actual destination
    Members("Tagok", LucideUsers, MembersDest),
    Programs("Programok", LucideCalendarDays, LoggedIn), // TODO: replace with actual destination

    // TODO: replace with actual destination
    Competition(
        "Pontverseny", LucidePodium, LoggedIn, arrayListOf(
            NavSubmenuItem("Összesítők", LoggedIn)// TODO: replace with actual destination
        )
    ),
}