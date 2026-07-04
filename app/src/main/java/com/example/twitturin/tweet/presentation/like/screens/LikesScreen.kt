package com.example.twitturin.tweet.presentation.like.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.twitturin.R
import com.example.twitturin.notification.presentation.screens.AnimatedPreloader
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.components.TweetItem
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikesScreen(
    modifier: Modifier = Modifier,
    viewModel: TweetViewModel,
    userId: String,
    onBack: () -> Unit,
    onTweetClick: (Tweet) -> Unit,
    onReplyClick: (Tweet) -> Unit,
    onShare: (Tweet) -> Unit
) {
    val response by viewModel.likedPosts.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val inProgress = stringResource(R.string.in_progress)

    LaunchedEffect(userId) { viewModel.getLikedPosts(userId) }
    LaunchedEffect(response) {
        val current = response
        if (current != null && !current.isSuccessful) {
            snackbarHostState.showSnackbar(current.body().toString())
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.likes)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val current = response
        val tweets = current?.body().orEmpty()
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when {
            current == null -> {
                Box(modifier = contentModifier, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            current.isSuccessful && tweets.isNotEmpty() -> {
                LazyColumn(modifier = contentModifier) {
                    items(tweets) { tweet ->
                        TweetItem(
                            tweet = tweet,
                            onClick = { onTweetClick(tweet) },
                            onHeart = { scope.launch { snackbarHostState.showSnackbar(inProgress) } },
                            onReply = { onReplyClick(tweet) },
                            onShare = { onShare(tweet) }
                        )
                        HorizontalDivider()
                    }
                }
            }

            else -> {
                Column(
                    modifier = contentModifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedPreloader(
                        modifier = Modifier.size(282.dp),
                        rawResId = R.raw.empty_likes_list
                    )
                    Text(
                        text = stringResource(R.string.profile_page_liked_tweets),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
