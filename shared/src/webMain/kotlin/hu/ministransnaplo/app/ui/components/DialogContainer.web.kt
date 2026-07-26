/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.Theme
import hu.ministransnaplo.app.ui.icons.lucide.LucideMenu
import hu.ministransnaplo.app.ui.icons.lucide.LucideX

@Composable
actual fun DialogContainer(
    title: String,
    webWidth: Dp,
    close: () -> Unit,
    navigate: (NavItem) -> Unit,
    trailingIcon: @Composable (() -> Unit),
    commands: DialogCommandRegistry.() -> Unit,
    content: @Composable () -> Unit
) {
    val commands = DialogCommandRegistry().apply(commands)
    var popupShown by remember { mutableStateOf(false) }
    var anchorBounds by remember { mutableStateOf(Rect.Zero) }
    Box(
        Modifier
            .clip(RoundedCornerShape(16f))
            .background(Theme.colorScheme.background)
            .padding(16.dp)
            .width(webWidth)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (commands.isNotEmpty) Image(
                        LucideMenu,
                        "",
                        Modifier.size(24.dp).clickable { popupShown = true }.onGloballyPositioned {
                            anchorBounds = it.boundsInWindow()
                        })
                    trailingIcon()
                    Image(LucideX, "", Modifier.size(24.dp).clickable(onClick = close))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
        if (popupShown) {
            Popup(
                onDismissRequest = { popupShown = false },
                properties = PopupProperties(focusable = true),
                popupPositionProvider = BelowAnchorEndAligned(anchorBounds)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E1E28),
                    shadowElevation = 8.dp
                ) {
                    Column(Modifier.padding(10.dp)) {
                        for (command in commands.commands) {
                            Row(Modifier.clickable {
                                popupShown = false
                                command.action()
                            }) {
                                Image(command.icon, command.title)
                                Spacer(Modifier.width(4.dp))
                                Text(command.title)
                            }
                        }
                    }
                }
            }
        }
    }
}

private class BelowAnchorEndAligned(
    private val anchorBounds: Rect
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,          // framework-supplied; ignored on purpose
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val bounds = this.anchorBounds   // use captured value explicitly

        val x = when (layoutDirection) {
            LayoutDirection.Ltr -> bounds.right - popupContentSize.width   // end = right
            LayoutDirection.Rtl -> bounds.left                              // end = left
        }
        var y = bounds.bottom  // directly below the anchor

        // Optional: flip above if there's no room below
        if (y + popupContentSize.height > windowSize.height) {
            y = bounds.top - popupContentSize.height
        }

        return IntOffset(
            x = x.coerceIn(0f, (windowSize.width - popupContentSize.width).coerceAtLeast(0).toFloat()).toInt(),
            y = y.coerceIn(0f, (windowSize.height - popupContentSize.height).coerceAtLeast(0).toFloat()).toInt()
        )
    }
}
