package com.example.twitturin.profile.presentation.screens

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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.twitturin.profile.domain.model.User
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.components.TweetItem
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel,
    tweetViewModel: TweetViewModel,
    userId: String,
    onShareProfile: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onFollowers: () -> Unit,
    onFollowing: () -> Unit,
    onTweetClick: (Tweet) -> Unit,
    onTweetShare: (Tweet) -> Unit
) {
    val credentials by profileViewModel.getUserCredentials.collectAsStateWithLifecycle()
    val userTweets by tweetViewModel.userTweets.observeAsState()
    val likedPosts by tweetViewModel.likedPosts.observeAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var menuOpen by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        profileViewModel.getUserCredentials(userId)
        tweetViewModel.getUserTweet(userId)
        tweetViewModel.getLikedPosts(userId)
    }

    val user = (credentials as? UserCredentials.Success)?.user
    val tweets = (if (selectedTab == 0) userTweets else likedPosts)?.body().orEmpty()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = user?.username?.let { "@$it" } ?: stringResource(R.string.profile)) },
                actions = {
                    IconButton(onClick = onShareProfile) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = null)
                    }
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(text = { Text("Edit profile") }, onClick = { menuOpen = false; onEditProfile() })
                        DropdownMenuItem(text = { Text(stringResource(R.string.logout)) }, onClick = { menuOpen = false; onLogout() })
                        DropdownMenuItem(text = { Text("Delete account") }, onClick = { menuOpen = false; onDeleteAccount() })
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item { ProfileHeader(user = user, onFollowers = onFollowers, onFollowing = onFollowing) }
            item {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(stringResource(R.string.tweets)) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(stringResource(R.string.likes)) })
                }
            }
            items(tweets) { tweet ->
                TweetItem(
                    tweet = tweet,
                    onClick = { onTweetClick(tweet) },
                    onHeart = {},
                    onReply = { onTweetClick(tweet) },
                    onShare = { onTweetShare(tweet) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    user: User?,
    onFollowers: () -> Unit,
    onFollowing: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = user?.profilePicture,
            contentDescription = null,
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = user?.fullName ?: stringResource(R.string.default_user_fullname),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "@${user?.username}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = user?.bio ?: stringResource(R.string.empty_bio),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        listOfNotNull(user?.kind, user?.studentId, user?.country, user?.birthday).forEach {
            Text(text = it, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(
                text = "${user?.followersCount ?: 0} followers",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable(onClick = onFollowers)
            )
            Text(
                text = "${user?.followingCount ?: 0} following",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable(onClick = onFollowing)
            )
        }
    }
}
