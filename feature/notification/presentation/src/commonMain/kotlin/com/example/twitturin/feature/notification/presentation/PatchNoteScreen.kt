package com.example.twitturin.feature.notification.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.designsystem.component.BrandTopBar
import com.example.twitturin.core.designsystem.component.LoadingBox
import com.example.twitturin.core.designsystem.component.MarkdownText
import com.example.twitturin.core.designsystem.component.PrimaryButton
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PatchNoteRoot(
    onBack: () -> Unit,
    viewModel: NotificationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is NotificationEvent.ShowError -> pendingError = event.message
            NotificationEvent.NavigateToPatchNote -> Unit // already on this screen
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    PatchNoteScreen(
        state = state,
        onBack = onBack,
        onUpdate = { url -> uriHandler.openUri(url) },
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun PatchNoteScreen(
    state: NotificationState,
    onBack: () -> Unit,
    onUpdate: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val release = state.release
    Scaffold(
        modifier = modifier,
        topBar = { BrandTopBar(title = release?.title ?: "Patch notes", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading && release == null -> LoadingBox()

                release == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No patch notes available", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                        ) {
                            MarkdownText(markdown = release.body.ifEmpty { "No release notes provided." })
                        }
                        release.downloadUrl?.let { url ->
                            PrimaryButton(
                                text = "Update",
                                onClick = { onUpdate(url) },
                                modifier = Modifier.padding(16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
