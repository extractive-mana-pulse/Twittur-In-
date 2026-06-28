package com.example.twitturin.feature.tweet.presentation.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.feature.tweet.presentation.components.TweetItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedRoot(
    onBack: () -> Unit,
    onOpenCompose: () -> Unit,
    onOpenTweet: (String) -> Unit,
    viewModel: FeedViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is FeedEvent.NavigateToTweet -> onOpenTweet(event.tweetId)
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
        onBack = onBack,
        onOpenCompose = onOpenCompose,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    state: FeedState,
    onAction: (FeedAction) -> Unit,
    onBack: () -> Unit,
    onOpenCompose: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Home") },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "Back") } },
                actions = { TextButton(onClick = { onAction(FeedAction.OnRefresh) }) { Text(text = "Refresh") } },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onOpenCompose) { Text(text = "Post") }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading && state.tweets.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.tweets.isEmpty() -> {
                    Text(
                        text = "No tweets yet",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    )
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.tweets, key = { it.id }) { tweet ->
                            TweetItem(
                                tweet = tweet,
                                onClick = { onAction(FeedAction.OnTweetClick(tweet.id)) },
                                headerAction = {
                                    if (tweet.isMine) {
                                        TextButton(onClick = { onAction(FeedAction.OnDeleteTweet(tweet.id)) }) {
                                            Text(text = "Delete")
                                        }
                                    }
                                },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
