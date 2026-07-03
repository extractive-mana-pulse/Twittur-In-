package com.example.twitturin.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.twitturin.core.designsystem.component.BottomNavItem
import com.example.twitturin.core.designsystem.component.TwitturBottomBar
import kotlinx.coroutines.launch

/**
 * Post-login shell chrome, identical on every platform: a persistent bottom navigation bar
 * (Home / Search / Notifications / Messages) plus a slide-out navigation drawer.
 *
 * [drawerContent] is the drawer body (profile header + items + version), built by the caller; it
 * receives a `closeDrawer` callback. [content] is the selected tab's screen; it receives
 * `openDrawer` (wired to the home tab's profile-picture button). [gesturesEnabled] lets the caller
 * restrict the swipe-to-open gesture to the home tab so the drawer is only reachable there.
 */
@Composable
fun AppNavigation(
    tabs: List<BottomNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    showLabels: Boolean,
    gesturesEnabled: Boolean,
    drawerContent: @Composable ColumnScope.(closeDrawer: () -> Unit) -> Unit,
    content: @Composable (openDrawer: () -> Unit) -> Unit,
) {
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
