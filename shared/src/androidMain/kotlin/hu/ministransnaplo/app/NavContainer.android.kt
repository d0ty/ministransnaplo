/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.ministransnaplo.app.ui.*
import hu.ministransnaplo.app.ui.icons.IconVariants
import hu.ministransnaplo.app.ui.icons.lucide.LucideEllipsis
import hu.ministransnaplo.app.ui.screens.LoggedIn

@Composable
private fun MenuItem(navMenuItem: NavMenuItem, activeMenu: NavMenuItem, onNavigation: () -> Unit) {
    val isActive = navMenuItem == activeMenu
    Column(
        modifier = Modifier.clickable { onNavigation() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            if (isActive) navMenuItem.icon.lightIcon else navMenuItem.icon.darkIcon,
            contentDescription = null, modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(1.dp))
        Text(
            navMenuItem.title, color = if (isActive) Color.White else Color.Black,
            fontWeight = FontWeight.SemiBold, fontSize = 15.sp
        )
    }
}

internal object MenuItem : NavMenuItem {
    override val title: String = "Menü"
    override val icon: IconVariants = LucideEllipsis
    override val destination: NavItem = LoggedIn
    override val submenuItems: ArrayList<NavSubmenuItem>
        get() = arrayListOf()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
actual fun NavContainer(
    viewModel: AppViewModel,
    onNavigation: (NavItem) -> Unit,
    content: @Composable () -> Unit,
) {
    var activeMenu: NavMenuItem by remember { mutableStateOf(NavMenuItems.Home) }
    Scaffold(bottomBar = {
        Row(
            Modifier.fillMaxWidth().background(Theme.colorScheme.primary).height(60.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavMenuItems.entries.forEach { item ->
                MenuItem(item, activeMenu, onNavigation = {
                    activeMenu = item
                    onNavigation(item.destination)
                })
            }
            MenuItem(MenuItem, activeMenu, onNavigation = {
                activeMenu = MenuItem
                onNavigation(MenuItem.destination)
            })
        }
    }) {
        content()
    }
}