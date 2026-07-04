package com.example.twitturin.detail.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.twitturin.R
import com.example.twitturin.core.extensions.formatCreatedAt
import com.example.twitturin.detail.presentation.sealed.PostReply
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.components.TweetItem
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    tweet: Tweet,
    viewModel: TweetViewModel,
    isOwnTweet: Boolean,
    onBack: () -> Unit,
    onMore: () -> Unit,
    onFollow: () -> Unit,
    onListOfLikes: () -> Unit,
    onSendReply: (String) -> Unit,
    onTweetClick: (Tweet) -> Unit,
    onTweetShare: (Tweet) -> Unit
) {
    val repliesResponse by viewModel.repliesOfPosts.collectAsStateWithLifecycle()
    val postReplyResult by viewModel.postReplyResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var replyText by rememberSaveable { mutableStateOf("") }
    val inProgress = stringResource(R.string.in_progress)

    LaunchedEffect(tweet.id) { tweet.id?.let { viewModel.getRepliesOfPost(it) } }
    LaunchedEffect(postReplyResult) {
        if (postReplyResult is PostReply.Success) {
            replyText = ""
            tweet.id?.let { viewModel.getRepliesOfPost(it) }
        }
    }

    val replies = repliesResponse?.body().orEmpty()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Post") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = onMore) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text(text = "Reply") }
                )
                IconButton(
                    enabled = replyText.isNotBlank(),
                    onClick = { onSendReply(replyText.trim()) }
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                DetailHeader(
                    tweet = tweet,
                    isOwnTweet = isOwnTweet,
                    onFollow = onFollow,
                    onHeart = { scope.launch { snackbarHostState.showSnackbar(inProgress) } },
                    onShare = { onTweetShare(tweet) },
                    onListOfLikes = onListOfLikes
                )
                HorizontalDivider()
            }
            items(replies) { reply ->
                TweetItem(
                    tweet = reply,
                    onClick = { onTweetClick(reply) },
                    onHeart = { scope.launch { snackbarHostState.showSnackbar(inProgress) } },
                    onReply = { onTweetClick(reply) },
                    onShare = { onTweetShare(reply) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun DetailHeader(
    tweet: Tweet,
    isOwnTweet: Boolean,
    onFollow: () -> Unit,
    onHeart: () -> Unit,
    onShare: () -> Unit,
    onListOfLikes: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = tweet.author?.profilePicture,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = tweet.author?.fullName ?: stringResource(R.string.default_user_fullname),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "@${tweet.author?.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = tweet.content.orEmpty(), style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tweet.createdAt?.formatCreatedAt().orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${tweet.likes ?: 0} likes",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable(onClick = onListOfLikes)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onHeart) {
                Icon(imageVector = Icons.Filled.FavoriteBorder, contentDescription = null)
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.Comment, contentDescription = null)
            IconButton(onClick = onShare) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = null)
            }
            if (!isOwnTweet) {
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = onFollow) {
                    Text(text = stringResource(R.string.follow))
                }
            }
        }
    }
}
