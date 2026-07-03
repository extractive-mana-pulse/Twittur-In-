package com.example.twitturin.tweet.presentation.postTweet.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Rendered as plain text (the legacy screen used Markwon; markdown markers are not styled here).
private const val POLICY_TEXT =
    "At TwitturIn, we are committed to protecting the privacy and security of our users. " +
    "This Privacy Policy explains how we collect, use, and protect your personal information " +
    "when you use our chat application.\n\n" +
    "Information We Collect\n\n" +
    "• User Account Information: When you create an account with us, we may collect your username, " +
    "email address, and other profile information you provide.\n\n" +
    "• Chat Messages: The content of the messages you send and receive through our chat app is stored " +
    "temporarily to enable the chat functionality. We do not access or read the content of your messages.\n\n" +
    "• Device Information: We may collect information about the device you use to access our app, such as " +
    "the device model, operating system, and IP address.\n\n" +
    "How We Use Your Information\n\n" +
    "• Account Management: We use your account information to create and maintain your user profile, " +
    "enable the chat functionality, and provide customer support.\n\n" +
    "• Improving Our Services: We may use the information we collect to analyze usage patterns and trends.\n\n" +
    "• Legal Compliance: We may use or disclose your information if we are required to do so by law.\n\n" +
    "Data Retention and Security\n\n" +
    "• Message Retention: Messages are automatically deleted after a retention period.\n\n" +
    "• Data Security: We implement appropriate technical and organizational measures to protect your data.\n\n" +
    "Your Rights and Choices\n\n" +
    "• Access and Correction, Deletion, and Opt-Out of certain communications.\n\n" +
    "Chat Etiquette and Guidelines: be respectful and inclusive, post appropriate content, communicate " +
    "constructively, protect privacy and security, cooperate with moderation, and use the app responsibly."

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicPostPolicyScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onAgree: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Privacy Policy") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = POLICY_TEXT,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            )
            Button(
                onClick = onAgree,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Agree")
            }
        }
    }
}
