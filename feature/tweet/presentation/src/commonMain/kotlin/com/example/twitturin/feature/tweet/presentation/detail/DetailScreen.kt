package com.example.twitturin.feature.tweet.presentation.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.feature.tweet.presentation.TweetUi
import com.example.twitturin.feature.tweet.presentation.components.TweetItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DetailRoot(
    tweetId: String,
    onBack: () -> Unit,
    onOpenTweet: (String) -> Unit,
    onOpenLikes: (String) -> Unit,
    viewModel: DetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    LaunchedEffect(tweetId) { viewModel.load(tweetId) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is DetailEvent.NavigateToTweet -> onOpenTweet(event.tweetId)
            is DetailEvent.NavigateToLikes -> onOpenLikes(event.tweetId)
            is DetailEvent.ShowError -> pendingError = event.message
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    DetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailState,
    onAction: (DetailAction) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Post") },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "Back") } },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.replyDraft,
                    onValueChange = { onAction(DetailAction.OnReplyChange(it)) },
                    placeholder = { Text(text = "Reply") },
                )
                TextButton(
                    enabled = state.replyDraft.isNotBlank() && !state.isSendingReply,
                    onClick = { onAction(DetailAction.OnSendReply(state.replyDraft)) },
                ) {
                    Text(text = "Send")
                }
            }
        },
    ) { innerPadding ->
        when {
            state.isLoading && state.tweet == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    state.tweet?.let { tweet ->
                        item {
                            DetailHeader(tweet = tweet, onOpenLikes = { onAction(DetailAction.OnOpenLikes) })
                            HorizontalDivider()
                        }
                    }
                    items(state.replies, key = { it.id }) { reply ->
                        TweetItem(
                            tweet = reply,
                            onClick = { onAction(DetailAction.OnReplyClick(reply.id)) },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHeader(tweet: TweetUi, onOpenLikes: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = tweet.authorAvatar,
                contentDescription = null,
                modifier = Modifier.size(48.dp).clip(CircleShape),
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = tweet.authorName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "@${tweet.authorUsername}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = tweet.content, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tweet.date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${tweet.likes} likes",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable(onClick = onOpenLikes),
        )
    }
}
