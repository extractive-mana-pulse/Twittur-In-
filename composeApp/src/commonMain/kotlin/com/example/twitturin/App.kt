package com.example.twitturin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Root composable shared across Android, iOS, Desktop and (later) Web.
 * Temporary "Hello" shell — replaced by the real NavHost/app graph during the migration.
 */
@Composable
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().safeContentPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Twittur — Compose Multiplatform")
            Text("Running on ${getPlatform().name}")
        }
    }
}
