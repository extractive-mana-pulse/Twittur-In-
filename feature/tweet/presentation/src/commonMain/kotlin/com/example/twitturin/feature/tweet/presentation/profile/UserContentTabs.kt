package com.example.twitturin.feature.tweet.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.designsystem.component.EmptyState
import com.example.twitturin.core.designsystem.component.LoadingBox
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.feature.tweet.presentation.components.TweetItem
import org.koin.compose.viewmodel.koinViewModel

/**
 * The Posts / Replies / Likes tabs shown under the profile header. Self-contained (owns its
 * [ProfileTweetsViewModel]); lives in :feature:tweet because it renders tweet cards, and is
 * composed into the profile screen via a slot so :feature:profile keeps no tweet dependency.
 */
@Composable
fun UserContentTabs(
    userId: String,
    onOpenTweet: (tweetId: String, focusReply: Boolean) -> Unit,
    onShare: (tweetId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileTweetsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(userId) { viewModel.load(userId) }

    Column(modifier = modifier.fillMaxSize()) {
        SecondaryTabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            ProfileTab.entries.forEach { tab ->
                val selected = tab == state.selectedTab
                Tab(
                    selected = selected,
                    onClick = { viewModel.selectTab(tab) },
                    text = {
                        Text(
                            text = tab.title,
                            color = if (selected) MaterialTheme.colorScheme.primary else SecondaryText,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            val list = state.currentList
            when {
                state.currentLoading && list.isEmpty() -> LoadingBox()

                list.isEmpty() -> EmptyState(
                    icon = TwitturIcons.Home,
                    title = "Nothing here yet",
                    subtitle = when (state.selectedTab) {
                        ProfileTab.POSTS -> "Posts will show up here."
                        ProfileTab.REPLIES -> "Replies will show up here."
                        ProfileTab.LIKES -> "Liked posts will show up here."
                    },
                )

                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(list, key = { it.id }) { tweet ->
                        TweetItem(
                            tweet = tweet,
                            onClick = { onOpenTweet(tweet.id, false) },
                            onReply = { onOpenTweet(tweet.id, true) },
                            onLike = { viewModel.toggleLike(tweet.id) },
                            onShare = { onShare(tweet.id) },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
