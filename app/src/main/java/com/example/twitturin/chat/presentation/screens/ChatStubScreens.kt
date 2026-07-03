package com.example.twitturin.chat.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.twitturin.R

@Composable
fun ChatScreen(modifier: Modifier = Modifier) {
    CenteredMessage(text = stringResource(R.string.fragment), modifier = modifier)
}

@Composable
fun ChatsScreen(modifier: Modifier = Modifier) {
    CenteredMessage(text = stringResource(R.string.there_is_no_any_conversations), modifier = modifier)
}

@Composable
fun GroupsScreen(modifier: Modifier = Modifier) {
    CenteredMessage(text = stringResource(R.string.there_is_no_any_group_conversations), modifier = modifier)
}

@Composable
private fun CenteredMessage(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
