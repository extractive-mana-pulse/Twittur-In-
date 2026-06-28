package com.example.twitturin.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(text = "Twittur") }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = "Welcome to Twittur", style = MaterialTheme.typography.headlineSmall)
            if (!userId.isNullOrBlank()) {
                Text(
                    text = "Signed in as $userId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onOpenFeed, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Feed")
            }
            Button(onClick = onOpenSearch, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Search")
            }
            Button(onClick = onOpenNotifications, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Notifications")
            }
            Button(onClick = onOpenProfile, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Profile")
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Log out")
            }
        }
    }
}
