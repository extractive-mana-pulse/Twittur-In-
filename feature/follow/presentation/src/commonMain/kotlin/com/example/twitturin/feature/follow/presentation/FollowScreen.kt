package com.example.twitturin.feature.follow.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.twitturin.core.designsystem.component.BrandTopBar
import com.example.twitturin.core.designsystem.component.FollowButton
import com.example.twitturin.core.designsystem.component.LottieAsset
import com.example.twitturin.core.designsystem.component.LottieEmptyState
import com.example.twitturin.core.designsystem.component.GradientAvatar
import com.example.twitturin.core.designsystem.component.LoadingBox
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import org.koin.compose.viewmodel.koinViewModel

/** Followers list for [userId]. Trailing button on each row = "Follow". */
@Composable
fun FollowersRoot(
    userId: String,
    onBack: () -> Unit,
    onOpenProfile: (String) -> Unit,
    viewModel: FollowViewModel = koinViewModel(),
) {
    FollowRoot(userId, FollowListMode.FOLLOWERS, onBack, onOpenProfile, viewModel)
}

/** Following list for [userId]. Trailing button on each row = "Unfollow". */
@Composable
fun FollowingRoot(
    userId: String,
    onBack: () -> Unit,
    onOpenProfile: (String) -> Unit,
    viewModel: FollowViewModel = koinViewModel(),
) {
    FollowRoot(userId, FollowListMode.FOLLOWING, onBack, onOpenProfile, viewModel)
}

@Composable
private fun FollowRoot(
    userId: String,
    mode: FollowListMode,
    onBack: () -> Unit,
    onOpenProfile: (String) -> Unit,
    viewModel: FollowViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingMessage by remember { mutableStateOf<UiText?>(null) }

    LaunchedEffect(userId, mode) { viewModel.load(userId, mode) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is FollowEvent.NavigateToProfile -> onOpenProfile(event.userId)
            is FollowEvent.ShowMessage -> pendingMessage = event.message
        }
    }

    pendingMessage?.let { message ->
        val text = message.asString()
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(text)
            pendingMessage = null
        }
    }

    FollowListScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListScreen(
    state: FollowState,
    onAction: (FollowAction) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    val title = when (state.mode) {
        FollowListMode.FOLLOWERS -> strings.followers
        FollowListMode.FOLLOWING -> strings.following
    }
    val emptyTitle = when (state.mode) {
        FollowListMode.FOLLOWERS -> "No followers yet"
        FollowListMode.FOLLOWING -> "Not following anyone yet"
    }

    Scaffold(
        modifier = modifier,
        topBar = { BrandTopBar(title = title, onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading && state.users.isEmpty() -> LoadingBox()

                state.hasLoaded && state.users.isEmpty() -> LottieEmptyState(
                    asset = LottieAsset.Person,
                    title = emptyTitle,
                    subtitle = strings.noUsersYet,
                    animationSize = 150.dp,
                )

                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.users) { user ->
                        FollowUserRow(
                            user = user,
                            following = state.mode == FollowListMode.FOLLOWING,
                            onClick = { onAction(FollowAction.OnUserClick(user.id)) },
                            onAction = { onAction(FollowAction.OnActionClick(user.id)) },
                            followText = strings.follow,
                            followingText = strings.unfollow,
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun FollowUserRow(
    user: FollowUserUi,
    following: Boolean,
    onClick: () -> Unit,
    onAction: () -> Unit,
    followText: String = "Follow",
    followingText: String = "Unfollow",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(48.dp)) {
            GradientAvatar(name = user.fullName, size = 48.dp)
            if (!user.profilePicture.isNullOrBlank()) {
                AsyncImage(
                    model = user.profilePicture,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(CircleShape),
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (user.bio.isNotBlank()) {
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        FollowButton(following = following, onClick = onAction, followText = followText, followingText = followingText)
    }
}
