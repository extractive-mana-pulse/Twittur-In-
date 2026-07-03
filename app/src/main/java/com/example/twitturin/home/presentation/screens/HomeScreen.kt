package com.example.twitturin.home.presentation.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.twitturin.R
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.components.TweetItem
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel,
    tweetViewModel: TweetViewModel,
    onAddPost: () -> Unit,
    onTweetClick: (Tweet) -> Unit,
    onTweetShare: (Tweet) -> Unit,
    onLanguage: () -> Unit,
    onTheme: () -> Unit,
    onTimetable: () -> Unit,
    onProfile: () -> Unit,
    onFeedback: () -> Unit,
    onSettings: () -> Unit
) {
    val credentials by profileViewModel.getUserCredentials.collectAsStateWithLifecycle()
    val response by tweetViewModel.responseOfTweet.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val user = (credentials as? UserCredentials.Success)?.user
    val developing = stringResource(R.string.developing)

    fun closeDrawerThen(action: () -> Unit) {
        scope.launch { drawerState.close() }
        action()
    }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(16.dp)) {
                    AsyncImage(
                        model = user?.profilePicture,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user?.fullName ?: stringResource(R.string.default_user_fullname),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "@${user?.username}", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = "${user?.followersCount ?: 0} followers", style = MaterialTheme.typography.bodySmall)
                        Text(text = "${user?.followingCount ?: 0} following", style = MaterialTheme.typography.bodySmall)
                    }
                }
                HorizontalDivider()
                NavigationDrawerItem(label = { Text(stringResource(R.string.profile)) }, selected = false, onClick = { closeDrawerThen(onProfile) })
                NavigationDrawerItem(label = { Text(stringResource(R.string.settings)) }, selected = false, onClick = { closeDrawerThen(onSettings) })
                NavigationDrawerItem(label = { Text(stringResource(R.string.feedback)) }, selected = false, onClick = { closeDrawerThen(onFeedback) })
                NavigationDrawerItem(label = { Text("Timetable") }, selected = false, onClick = { closeDrawerThen(onTimetable) })
                NavigationDrawerItem(label = { Text("Language") }, selected = false, onClick = { closeDrawerThen(onLanguage) })
                NavigationDrawerItem(label = { Text("Theme") }, selected = false, onClick = { closeDrawerThen(onTheme) })
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddPost) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            val tweets = response?.body().orEmpty()
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

            if (response == null) {
                Box(modifier = contentModifier, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = contentModifier) {
                    items(tweets) { tweet ->
                        TweetItem(
                            tweet = tweet,
                            onClick = { onTweetClick(tweet) },
                            onHeart = { scope.launch { snackbarHostState.showSnackbar(developing) } },
                            onReply = { onTweetClick(tweet) },
                            onShare = { onTweetShare(tweet) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
