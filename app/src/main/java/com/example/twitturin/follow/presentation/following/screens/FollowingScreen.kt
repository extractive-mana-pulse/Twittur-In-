package com.example.twitturin.follow.presentation.following.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.twitturin.R
import com.example.twitturin.follow.presentation.components.EmptyFollowState
import com.example.twitturin.follow.presentation.components.FollowUserRow
import com.example.twitturin.follow.presentation.following.sealed.UnFollow
import com.example.twitturin.follow.presentation.vm.FollowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowingScreen(
    modifier: Modifier = Modifier,
    viewModel: FollowViewModel,
    userId: String,
    token: String,
    onBack: () -> Unit,
    onUserClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val followingResponse by viewModel.followingList.observeAsState()
    val unFollowResult by viewModel.unFollow.observeAsState()

    LaunchedEffect(userId) { viewModel.getFollowing(userId) }

    LaunchedEffect(unFollowResult) {
        when (val result = unFollowResult) {
            is UnFollow.Success -> snackbarHostState.showSnackbar("you unfollow: ${result.user.username}")
            is UnFollow.Error -> snackbarHostState.showSnackbar(result.message)
            else -> Unit
        }
    }

    LaunchedEffect(followingResponse) {
        val response = followingResponse
        if (response != null && !response.isSuccessful) {
            snackbarHostState.showSnackbar(response.message())
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.following)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val response = followingResponse
        val users = response?.body().orEmpty()
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when {
            response == null -> {
                Box(modifier = contentModifier, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            response.isSuccessful && users.isNotEmpty() -> {
                LazyColumn(modifier = contentModifier) {
                    items(users) { user ->
                        FollowUserRow(
                            user = user,
                            actionLabel = stringResource(R.string.unfollow),
                            onClick = onUserClick,
                            onAction = { user.id?.let { viewModel.unFollowUser(it, token) } }
                        )
                        HorizontalDivider()
                    }
                }
            }

            else -> {
                EmptyFollowState(
                    message = stringResource(R.string.user_following_information),
                    modifier = contentModifier
                )
            }
        }
    }
}
