package com.example.twitturin.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.component.BottomNavItem
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
    if (getPlatform().isDesktop) {
        DesktopRailShell(
            tabs = tabs,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            showLabels = showLabels,
            railActions = railActions,
            content = content,
        )
        return
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
        // Plain Column (not a Scaffold) so the tab screens' own Scaffolds own the top/system insets
        // — avoids the double-inset that nesting Scaffolds would cause. The bottom bar gets the
        // navigation-bar inset for edge-to-edge.
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                content { scope.launch { drawerState.open() } }
            }
            TwitturBottomBar(
                items = tabs,
                selectedIndex = selectedIndex,
                onSelect = onSelect,
                showLabels = showLabels,
            )
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
                    icon = { Icon(item.icon, contentDescription = item.label) },
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
