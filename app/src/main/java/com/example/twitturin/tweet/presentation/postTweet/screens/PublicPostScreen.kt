package com.example.twitturin.tweet.presentation.postTweet.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.twitturin.tweet.presentation.postTweet.sealed.PostTweet
import com.example.twitturin.tweet.presentation.postTweet.vm.PostTweetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicPostScreen(
    modifier: Modifier = Modifier,
    viewModel: PostTweetViewModel,
    onBack: () -> Unit,
    onPost: (String) -> Unit,
    onPosted: () -> Unit
) {
    var content by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }
    val result by viewModel.postTweetResult.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(result, submitted) {
        if (!submitted) return@LaunchedEffect
        when (val current = result) {
            is PostTweet.Success -> onPosted()
            is PostTweet.Error -> {
                snackbarHostState.showSnackbar(current.message)
                submitted = false
            }
            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "New post") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(
                        enabled = content.isNotBlank() && !submitted,
                        onClick = {
                            submitted = true
                            onPost(content)
                        }
                    ) {
                        Text(text = "Tweet")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                TextButton(onClick = { content += "**" }) {
                    Text(text = "B", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { content = "*$content*" }) {
                    Text(text = "I", fontStyle = FontStyle.Italic)
                }
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                value = content,
                onValueChange = { content = it },
                placeholder = { Text(text = "What's happening?") }
            )
        }
    }
}
