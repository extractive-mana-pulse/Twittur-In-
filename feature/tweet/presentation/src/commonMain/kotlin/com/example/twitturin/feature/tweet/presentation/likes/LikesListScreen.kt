package com.example.twitturin.feature.tweet.presentation.likes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.designsystem.component.BrandTopBar
import com.example.twitturin.core.designsystem.component.LoadingBox
import com.example.twitturin.core.designsystem.component.LottieAsset
import com.example.twitturin.core.designsystem.component.LottieEmptyState
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.feature.tweet.presentation.components.TweetAvatar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LikesListRoot(
    tweetId: String,
    onBack: () -> Unit,
    viewModel: LikesListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    LaunchedEffect(tweetId) { viewModel.load(tweetId) }

    ObserveAsEvents(viewModel.errors) { error -> pendingError = error }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    LikesListScreen(
        state = state,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun LikesListScreen(
    state: LikesListState,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { BrandTopBar(title = LocalStrings.current.likes, onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading -> LoadingBox()

                state.likers.isEmpty() -> LottieEmptyState(
                    asset = LottieAsset.EmptyLikes,
                    title = "No likes yet",
                    subtitle = "Be the first to like this post.",
                )

                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.likers, key = { it.id }) { liker ->
                        LikerRow(liker)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun LikerRow(liker: LikerUi) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TweetAvatar(name = liker.fullName, avatarUrl = liker.avatar, size = 48.dp)
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = liker.fullName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "@${liker.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
            )
        }
    }
}
