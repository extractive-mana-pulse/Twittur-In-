package com.example.twitturin.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.twitturin.core.designsystem.component.BottomNavItem
import com.example.twitturin.core.designsystem.component.PrimaryButton
import com.example.twitturin.core.designsystem.component.TwitturBottomBar
import com.example.twitturin.core.designsystem.component.TwitturLogo
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.SecondaryText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoot(
    onOpenFeed: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenProfile: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    HomeScreen(
        userId = viewModel.userId,
        onOpenFeed = onOpenFeed,
        onOpenSearch = onOpenSearch,
        onOpenNotifications = onOpenNotifications,
        onOpenProfile = onOpenProfile,
        onLogout = {
            viewModel.logout()
            onLoggedOut()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: String?,
    onOpenFeed: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenProfile: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        BottomNavItem(TwitturIcons.Home, "Home"),
        BottomNavItem(TwitturIcons.Search, "Search"),
        BottomNavItem(TwitturIcons.Notifications, "Notifications"),
        BottomNavItem(TwitturIcons.Account, "Profile"),
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { TwitturLogo(fontSize = 24.sp) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(TwitturIcons.Logout, contentDescription = "Log out")
                    }
                },
            )
        },
        bottomBar = {
            TwitturBottomBar(
                items = items,
                selectedIndex = 0,
                onSelect = { index ->
                    when (index) {
                        0 -> onOpenFeed()
                        1 -> onOpenSearch()
                        2 -> onOpenNotifications()
                        3 -> onOpenProfile()
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TwitturLogo(fontSize = 40.sp)
            Text(
                text = if (!userId.isNullOrBlank()) "You're signed in. Jump into your feed." else "Welcome to TwitturIn.",
                style = MaterialTheme.typography.bodyLarge,
                color = SecondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp, bottom = 28.dp),
            )
            PrimaryButton(text = "Open your feed", onClick = onOpenFeed)
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}
