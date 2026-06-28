package com.example.twitturin.feature.tweet.presentation.likes

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikesListScreen(
    state: LikesListState,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Likes") },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "Back") } },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                state.likers.isEmpty() -> Text(
                    text = "No likes yet",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
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
        AsyncImage(
            model = liker.avatar,
            contentDescription = null,
            modifier = Modifier.size(48.dp).clip(CircleShape),
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = liker.fullName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "@${liker.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
