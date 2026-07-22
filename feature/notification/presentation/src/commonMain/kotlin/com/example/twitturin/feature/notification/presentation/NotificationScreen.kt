package com.example.twitturin.feature.notification.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.designsystem.component.LoadingBox
import com.example.twitturin.core.designsystem.component.LottieAsset
import com.example.twitturin.core.designsystem.component.LottieEmptyState
import com.example.twitturin.core.designsystem.component.PrimaryButton
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.DividerLine
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationRoot(
    onNavigateToPatchNote: () -> Unit,
    onMenu: (() -> Unit)? = null,
    viewModel: NotificationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            NotificationEvent.NavigateToPatchNote -> onNavigateToPatchNote()
            is NotificationEvent.ShowError -> pendingError = event.message
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    NotificationScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        onMenu = onMenu,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    state: NotificationState,
    onAction: (NotificationAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onMenu: (() -> Unit)? = null,
) {
    val strings = LocalStrings.current
    // If notifications aren't allowed yet, surface the system permission dialog on entry.
    RequestNotificationPermissionEffect()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = strings.notifications, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    if (onMenu != null) {
                        IconButton(onClick = onMenu) {
                            Icon(TwitturIcons.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading -> LoadingBox()

                state.release == null -> LottieEmptyState(
                    asset = LottieAsset.EmptyNotifications,
                    title = strings.caughtUpTitle,
                    subtitle = strings.caughtUpSubtitle,
                )

                else -> ReleaseContent(release = state.release, onAction = onAction)
            }
        }
    }
}

@Composable
private fun ReleaseContent(
    release: ReleaseUi,
    onAction: (NotificationAction) -> Unit,
) {
    val strings = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, DividerLine, RoundedCornerShape(14.dp))
                .padding(20.dp),
        ) {
            Text(text = release.title, style = MaterialTheme.typography.titleLarge)
            if (release.tagName.isNotEmpty()) {
                Text(
                    text = "${strings.versionLabel} ${release.tagName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            if (release.publishedAt.isNotEmpty()) {
                Text(
                    text = "${strings.publishedLabel} ${release.publishedAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            PrimaryButton(text = strings.viewPatchNotes, onClick = { onAction(NotificationAction.OnPatchNoteClick) })
        }
    }
}
