package com.example.twitturin.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.component.BottomBarVisibilityController
import com.example.twitturin.core.designsystem.component.BottomNavItem
import com.example.twitturin.core.designsystem.component.LocalBottomBarVisibility
import com.example.twitturin.core.designsystem.component.NavBadge
import com.example.twitturin.core.designsystem.component.TwitturBottomBar
import com.example.twitturin.core.designsystem.theme.DividerLine
import com.example.twitturin.getPlatform
import kotlinx.coroutines.launch

/** A secondary destination on the desktop navigation rail. Keep [onClick] last (trailing lambda). */
data class RailAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

/**
 * Post-login shell chrome.
 *
 * Touch platforms (Android / iOS): a persistent bottom navigation bar (Home / Search /
 * Notifications / Messages) plus a slide-out navigation drawer. [drawerContent] is the drawer
 * body (profile header + items + version), built by the caller; it receives a `closeDrawer`
 * callback. [gesturesEnabled] restricts the swipe-to-open gesture to the home tab.
 *
 * Desktop: a left [NavigationRail] instead of the drawer — the four tabs on top, the drawer's
 * secondary destinations ([railActions]) pinned to the bottom.
 *
 * [content] is the selected tab's screen; it receives `openDrawer`, which is null on desktop
 * (no drawer to open — the home tab hides its menu button).
 */
@Composable
fun AppNavigation(
    tabs: List<BottomNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    showLabels: Boolean,
    gesturesEnabled: Boolean,
    railActions: List<RailAction>,
    drawerContent: @Composable ColumnScope.(closeDrawer: () -> Unit) -> Unit,
    content: @Composable (openDrawer: (() -> Unit)?) -> Unit,
) {
    // One controller for the whole shell so a full-screen tab (e.g. the timetable in landscape)
    // can hide/reveal the shared bottom bar on scroll; provided to every screen below.
    val bottomBarController = remember { BottomBarVisibilityController() }

    CompositionLocalProvider(LocalBottomBarVisibility provides bottomBarController) {
        if (getPlatform().isDesktop) {
            DesktopRailShell(
                tabs = tabs,
                selectedIndex = selectedIndex,
                onSelect = onSelect,
                showLabels = showLabels,
                railActions = railActions,
                content = content,
            )
            return@CompositionLocalProvider
        }

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            drawerContent = {
                ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.background) {
                    drawerContent { scope.launch { drawerState.close() } }
                }
            },
        ) {
            // Plain Column (not a Scaffold) so the tab screens' own Scaffolds own the top/system
            // insets — avoids the double-inset that nesting Scaffolds would cause. The bottom bar
            // consumes the navigation-bar inset itself, so the tab content must NOT apply it again:
            // consuming it here stops the inner Scaffolds from adding an extra strip of padding.
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .consumeWindowInsets(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
                ) {
                    content { scope.launch { drawerState.open() } }
                }
                // The bar collapses its height (not just a slide) so the content above smoothly
                // reclaims the space rather than snapping when the animation ends.
                AnimatedVisibility(
                    visible = bottomBarController.isVisible,
                    enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
                ) {
                    TwitturBottomBar(
                        items = tabs,
                        selectedIndex = selectedIndex,
                        onSelect = onSelect,
                        showLabels = showLabels,
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopRailShell(
    tabs: List<BottomNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    showLabels: Boolean,
    railActions: List<RailAction>,
    content: @Composable (openDrawer: (() -> Unit)?) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(containerColor = MaterialTheme.colorScheme.background) {
            Spacer(Modifier.height(8.dp))
            tabs.forEachIndexed { index, item ->
                NavigationRailItem(
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) },
                    icon = {
                        Box {
                            Icon(item.icon, contentDescription = item.label)
                            item.badge?.let { badge ->
                                NavBadge(text = badge, modifier = Modifier.align(Alignment.TopEnd).offset(x = 9.dp, y = (-4).dp))
                            }
                        }
                    },
                    label = if (showLabels) {
                        { Text(item.label) }
                    } else null,
                )
            }
            Spacer(Modifier.weight(1f))
            railActions.forEach { action ->
                NavigationRailItem(
                    selected = false,
                    onClick = action.onClick,
                    icon = { Icon(action.icon, contentDescription = action.label) },
                    label = if (showLabels) {
                        { Text(action.label) }
                    } else null,
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        VerticalDivider(color = DividerLine)
        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
            content(null)
        }
    }
}
