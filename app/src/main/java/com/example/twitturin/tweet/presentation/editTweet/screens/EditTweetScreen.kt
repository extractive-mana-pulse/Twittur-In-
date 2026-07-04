package com.example.twitturin.tweet.presentation.editTweet.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import com.example.twitturin.tweet.presentation.editTweet.sealed.EditTweet
import com.example.twitturin.tweet.presentation.editTweet.vm.EditTweetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTweetScreen(
    modifier: Modifier = Modifier,
    viewModel: EditTweetViewModel,
    initialContent: String,
    tweetId: String,
    token: String,
    onClose: () -> Unit
) {
    var content by rememberSaveable { mutableStateOf(initialContent) }
    var submitting by rememberSaveable { mutableStateOf(false) }
    val result by viewModel.editTweetResult.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(result) {
        when (val current = result) {
            is EditTweet.Success -> onClose()
            is EditTweet.Error -> {
                snackbarHostState.showSnackbar(current.error)
                submitting = false
            }
            null -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit tweet") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(
                        enabled = content.isNotBlank() && !submitting,
                        onClick = {
                            submitting = true
                            viewModel.editTweet(content.trim(), tweetId, token)
                        }
                    ) {
                        Text(text = "Update")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        OutlinedTextField(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            value = content,
            onValueChange = { content = it },
            label = { Text(text = "Tweet") }
        )
    }
}
