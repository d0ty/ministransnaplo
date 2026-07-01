/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.ui.*
import hu.ministransnaplo.app.ui.icons.AppLogo
import hu.ministransnaplo.app.ui.icons.IconVariants
import hu.ministransnaplo.app.ui.icons.iconVariants
import hu.ministransnaplo.app.ui.icons.lucide.*
import hu.ministransnaplo.app.ui.screens.LoggedIn
import hu.ministransnaplo.app.ui.screens.auth.Login
import hu.ministransnaplo.app.ui.screens.auth.mfa.enroll.MFAEnroll
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
internal fun MenuItem(navMenuItem: NavMenuItem, onNavigation: (NavItem) -> Unit) {
    val hasSubmenu = navMenuItem.submenuItems.isNotEmpty()
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.padding(0.dp, 4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f).clickable { onNavigation(navMenuItem.destination) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    navMenuItem.icon.lightIcon,
                    contentDescription = null, modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    navMenuItem.title, color = Color.White,
                    fontWeight = FontWeight.SemiBold, fontSize = 20.sp
                )
            }
            if (hasSubmenu) {
                Image(LucideChevronDown, contentDescription = null, modifier = Modifier.size(32.dp).clickable {
                    expanded = !expanded
                })
            }
        }
        AnimatedVisibility(expanded) {
            Column {
                Spacer(Modifier.height(4.dp))
                navMenuItem.submenuItems.forEach { submenuItem ->
                    Row(
                        Modifier.fillMaxWidth().padding(46.dp, 0.dp)
                            .clickable { onNavigation(submenuItem.destination) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            submenuItem.title, color = Color.White,
                            fontWeight = FontWeight.Normal, fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

object SettingsMenuItem : NavMenuItem {
    override val title: String = "Beállítások"
    override val icon: IconVariants = LucideBolt.iconVariants()
    override val destination: NavItem = LoggedIn

    // TODO: Actual destinations should be provided
    override val submenuItems: ArrayList<NavSubmenuItem>
        get() = arrayListOf(
            NavSubmenuItem("Gárdaprofil"),
            NavSubmenuItem("Pontverseny"),
            NavSubmenuItem("Szankciók"),
            NavSubmenuItem("Vezetők"),
        )
}

@Composable
fun UserInfo(userState: AppViewModel.UserState) {
    Row {
        Image(LucideUserRound, contentDescription = null, modifier = Modifier.size(48.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(userState.displayName, fontSize = 16.sp)
            Text(userState.email, fontSize = 14.sp)
        }
    }
}

@Composable
fun ProfileAction(icon: ImageVector, label: String, onClicked: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().clickable { onClicked() }, verticalAlignment = Alignment.CenterVertically) {
            Image(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, Color(0xFF404040))
    }
}


@Composable
actual fun NavContainer(
    viewModel: AppViewModel,
    onNavigation: (NavItem) -> Unit,
    content: @Composable () -> Unit,
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    var profileExpanded by remember { mutableStateOf(false) }
    var anchorBounds by remember { mutableStateOf(Rect.Zero) }
    Box {
        Row(Modifier.fillMaxSize()) {
            //Sidebar
            Column(
                Modifier.width(350.dp).fillMaxHeight().background(Color(0xFF100E17)).padding(20.dp, 16.dp, 16.dp, 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(AppLogo, contentDescription = "", modifier = Modifier.height(60.dp).width(41.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Ministráns Napló", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        userState.guard?.ProfilePicture(viewModel.supabase, size = 42.dp)
                        Spacer(Modifier.width(10.dp))
                        Text(userState.guard?.name ?: "Gárda", fontWeight = FontWeight.Medium, fontSize = 24.sp)
                    }
                    Spacer(Modifier.height(30.dp))
                    Column {
                        NavMenuItems.entries.forEach { item ->
                            MenuItem(item, onNavigation = onNavigation)
                        }
                        MenuItem(SettingsMenuItem, onNavigation = onNavigation)
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                        .onGloballyPositioned {
                            anchorBounds = it.boundsInWindow()
                        }
                ) {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(Theme.colorScheme.background)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        UserInfo(userState)
                        Image(
                            LucideChevronDown,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp).clickable {
                                profileExpanded = true
                            })
                    }
                }
            }

            // Content
            Column(Modifier.weight(1f)) {
                content()
            }
        }
        if (profileExpanded) {
            Popup(
                onDismissRequest = { profileExpanded = false },
                properties = PopupProperties(focusable = true),
                popupPositionProvider = AboveAnchorLeftAligned(anchorBounds)
            ) {
                Surface(
                    modifier = Modifier.width(with(LocalDensity.current) { anchorBounds.width.toDp() }),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E1E28),
                    shadowElevation = 8.dp
                ) {
                    Column {
                        UserInfo(userState)
                        HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, Color(0xFF404040))
                        ProfileAction(LucideUserRoundKey, "2. faktor visszaállítása", onClicked = {
                            profileExpanded = false
                            onNavigation(MFAEnroll)
                        })
                        ProfileAction(LucideRotateCcwKey, "Jelszó visszaállítása", onClicked = {
                            profileExpanded = false
                            // TODO: Password reset
                        })
                        ProfileAction(LucideLogOut, "Kijelentkezés", onClicked = {
                            viewModel.viewModelScope.launch {
                                viewModel.supabase.auth.signOut()
                                onNavigation(Login)
                            }
                        })
                    }
                }
            }
        }
    }
}

class AboveAnchorLeftAligned(
    private val capturedAnchorBounds: Rect,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        var x = capturedAnchorBounds.right                              // start just right of anchor
        var y = capturedAnchorBounds.top                                // top-align with anchor

        // Flip to the left side if there's no room on the right
        if (x + popupContentSize.width > windowSize.width) {
            x = capturedAnchorBounds.left - popupContentSize.width
        }

        x = x.coerceIn(0f, (windowSize.width - popupContentSize.width).coerceAtLeast(0).toFloat())
        y = y.coerceIn(0f, (windowSize.height - popupContentSize.height).coerceAtLeast(0).toFloat())

        return IntOffset(x.toInt(), y.toInt())
    }
}