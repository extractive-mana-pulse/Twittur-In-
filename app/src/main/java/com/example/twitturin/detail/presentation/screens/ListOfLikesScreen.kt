package com.example.twitturin.detail.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.twitturin.R
import com.example.twitturin.detail.domain.model.UserLikesAPost
import com.example.twitturin.detail.presentation.vm.ListOfLikesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfLikesScreen(
    modifier: Modifier = Modifier,
    viewModel: ListOfLikesViewModel,
    tweetId: String,
    onBack: () -> Unit
) {
    val response by viewModel.listOfLikesToThePosts.observeAsState()

    LaunchedEffect(tweetId) { viewModel.getListOfUsersLikesAPost(tweetId) }

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
        }
    ) { innerPadding ->
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

            users.isEmpty() -> {
                Box(modifier = contentModifier, contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.in_progress))
                }
            }

            else -> {
                LazyColumn(modifier = contentModifier) {
                    items(users) { user ->
                        LikeUserRow(user)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun LikeUserRow(user: UserLikesAPost) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.profilePicture,
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = user.fullName ?: stringResource(R.string.default_user_fullname),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
