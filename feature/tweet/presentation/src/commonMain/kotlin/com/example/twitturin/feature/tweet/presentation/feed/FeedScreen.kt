package com.example.twitturin.feature.tweet.presentation.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.designsystem.component.LoadingBox
import com.example.twitturin.core.designsystem.component.LottieAsset
import com.example.twitturin.core.designsystem.component.LottieEmptyState
import com.example.twitturin.core.designsystem.component.TwitturExtendedFab
import com.example.twitturin.core.designsystem.component.TwitturFab
import com.example.twitturin.core.designsystem.component.TwitturLogo
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.feature.tweet.presentation.components.TweetAvatar
import com.example.twitturin.feature.tweet.presentation.components.TweetItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedRoot(
    onOpenCompose: () -> Unit,
    onOpenTweet: (tweetId: String, focusReply: Boolean) -> Unit,
    onShare: (tweetId: String) -> Unit = {},
    onOpenProfile: (userId: String) -> Unit = {},
    expandedFab: Boolean = false,
    onBack: (() -> Unit)? = null,
    onMenu: (() -> Unit)? = null,
    menuAvatarUrl: String? = null,
    menuAvatarName: String = "",
    viewModel: FeedViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is FeedEvent.NavigateToTweet -> onOpenTweet(event.tweetId, event.focusReply)
            is FeedEvent.ShowError -> pendingError = event.message
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    FeedScreen(
        state = state,
        onAction = viewModel::onAction,
        onShare = onShare,
        onOpenProfile = onOpenProfile,
        onBack = onBack,
        onMenu = onMenu,
        menuAvatarUrl = menuAvatarUrl,
        menuAvatarName = menuAvatarName,
        expandedFab = expandedFab,
        onOpenCompose = onOpenCompose,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    state: FeedState,
    onAction: (FeedAction) -> Unit,
    onOpenCompose: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onShare: (tweetId: String) -> Unit = {},
    onOpenProfile: (userId: String) -> Unit = {},
    onBack: (() -> Unit)? = null,
    onMenu: (() -> Unit)? = null,
    menuAvatarUrl: String? = null,
    menuAvatarName: String = "",
    expandedFab: Boolean = false,
) {
    val strings = LocalStrings.current
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { TwitturLogo(fontSize = 24.sp) },
                navigationIcon = {
                    when {
                        // Profile-picture button opens the navigation drawer (home only).
                        onMenu != null -> Box(
                            modifier = Modifier.padding(start = 12.dp).clickable(onClick = onMenu),
                        ) {
                            TweetAvatar(name = menuAvatarName, avatarUrl = menuAvatarUrl, size = 34.dp)
                        }
                        onBack != null -> IconButton(onClick = onBack) {
                            Icon(TwitturIcons.Back, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(),
            )
        },
        floatingActionButton = {
            // The Settings toggle switches between the default square FAB and the expanded one.
            if (expandedFab) {
                TwitturExtendedFab(text = strings.newPost, onClick = onOpenCompose)
            } else {
                TwitturFab(onClick = onOpenCompose)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading && state.tweets.isEmpty() -> LoadingBox()

                state.tweets.isEmpty() -> LottieEmptyState(
                    asset = LottieAsset.EmptyTweets,
                    title = strings.noTweetsTitle,
                    subtitle = strings.noTweetsSubtitle,
                )

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.tweets, key = { it.id }) { tweet ->
                            TweetItem(
                                tweet = tweet,
                                onClick = { onAction(FeedAction.OnTweetClick(tweet.id)) },
                                onReply = { onAction(FeedAction.OnReplyClick(tweet.id)) },
                                onLike = { onAction(FeedAction.OnLikeClick(tweet.id)) },
                                onShare = { onShare(tweet.id) },
                                // Avatar goes to the author's profile, not the tweet detail.
                                onAvatarClick = { onOpenProfile(tweet.authorId) },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
