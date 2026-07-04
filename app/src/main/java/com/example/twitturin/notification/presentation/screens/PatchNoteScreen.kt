package com.example.twitturin.notification.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.notification.presentation.vm.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchNoteScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel,
    onBack: () -> Unit,
    onUpdate: (String) -> Unit
) {
    val response by viewModel.gitResponse.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.getLatestRelease() }

    val release = response?.body()
    val downloadUrl = release?.assets?.firstOrNull()?.browserDownloadUrl

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = release?.name ?: release?.tagName ?: "Patch notes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = release?.body.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            )
            if (downloadUrl != null) {
                Button(
                    onClick = { onUpdate(downloadUrl) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Update")
                }
            }
        }
    }
}
