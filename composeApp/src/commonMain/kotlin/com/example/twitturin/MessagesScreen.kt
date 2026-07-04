package com.example.twitturin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.twitturin.core.designsystem.component.BrandTopBar
import com.example.twitturin.core.designsystem.component.EmptyState
import com.example.twitturin.core.designsystem.icon.TwitturIcons

/** Placeholder Messages tab — direct messages (chat) aren't ported to Track A yet. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = { BrandTopBar(title = "Messages") },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            EmptyState(
                icon = TwitturIcons.Mail,
                title = "No messages yet",
                subtitle = "Direct messages are coming soon.",
            )
        }
    }
}
