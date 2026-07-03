package com.example.twitturin.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.BrandSoft
import com.example.twitturin.core.designsystem.theme.OnBrand
import com.example.twitturin.core.designsystem.theme.SecondaryText

/** Brand top app bar — back arrow + title + optional trailing actions. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(TwitturIcons.Back, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

/** Top bar with a title + trailing overflow (3-dot) menu. [menu] supplies DropdownMenuItem rows. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwitturTopBarMore(
    title: String,
    menuExpanded: Boolean,
    onMenuClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    menu: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onMenu: (() -> Unit)? = null,
    navigationContent: (@Composable () -> Unit)? = null,
    extraActions: @Composable () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            when {
                navigationContent != null -> navigationContent()
                onMenu != null -> IconButton(onClick = onMenu) {
                    Icon(TwitturIcons.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
                }
                onBack != null -> IconButton(onClick = onBack) {
                    Icon(TwitturIcons.Back, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        actions = {
            extraActions()
            IconButton(onClick = onMenuClick) {
                Icon(TwitturIcons.More, contentDescription = "More", tint = MaterialTheme.colorScheme.onSurface)
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = onMenuDismiss, content = menu)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

data class BottomNavItem(val icon: ImageVector, val label: String)

/**
 * Bottom navigation bar — nav-surface background; the active tab gets an accent-tinted pill.
 * The accent follows the theme [primary][androidx.compose.material3.ColorScheme.primary] so it
 * reflects the user's chosen colour. When [showLabels] is true a small caption sits under each icon.
 */
@Composable
fun TwitturBottomBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showLabels: Boolean = false,
) {
    val accent = MaterialTheme.colorScheme.primary
    val pill = MaterialTheme.colorScheme.primaryContainer
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .height(if (showLabels) 72.dp else 64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .then(if (selected) Modifier.background(pill, RoundedCornerShape(18.dp)) else Modifier)
                        .padding(horizontal = if (showLabels) 14.dp else 22.dp, vertical = 6.dp)
                        .clickableNoRipple { onSelect(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) accent else SecondaryText,
                        modifier = Modifier.size(26.dp),
                    )
                    if (showLabels) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) accent else SecondaryText,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

/** Square-ish FAB (54dp, 18dp corners) for "compose"; fill follows the theme accent. */
@Composable
fun TwitturFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = TwitturIcons.Add,
) {
    Box(
        modifier = modifier
            .size(54.dp)
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp))
            .clickableNoRipple(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = "Create", tint = OnBrand, modifier = Modifier.size(28.dp))
    }
}
