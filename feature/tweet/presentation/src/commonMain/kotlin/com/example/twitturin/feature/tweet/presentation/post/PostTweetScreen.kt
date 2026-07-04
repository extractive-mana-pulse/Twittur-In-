package com.example.twitturin.feature.tweet.presentation.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.richtexteditor.components.RichTextField
import com.example.richtexteditor.components.RichTextToolbar
import com.example.richtexteditor.rememberRichTextEditorController
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.BrandSoft
import com.example.twitturin.core.designsystem.theme.Danger
import com.example.twitturin.core.designsystem.theme.DividerLine
import com.example.twitturin.core.designsystem.theme.OnBrand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

private const val MAX_TWEET_LENGTH = 280
private const val IMAGES_UNAVAILABLE = "Publishing pictures isn't available yet."

@Composable
fun PostTweetRoot(
    onBack: () -> Unit,
    onPosted: () -> Unit,
    tweetId: String? = null,
    initialText: String? = null,
    recentImages: List<String> = emptyList(),
    viewModel: PostTweetViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            PostTweetEvent.Posted -> onPosted()
            is PostTweetEvent.ShowError -> pendingError = event.message
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    PostTweetScreen(
        state = state,
        isEditing = tweetId != null,
        initialText = initialText.orEmpty(),
        recentImages = recentImages,
        onBack = onBack,
        onPost = { content -> viewModel.post(content, tweetId) },
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTweetScreen(
    state: PostTweetState,
    onBack: () -> Unit,
    onPost: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false,
    initialText: String = "",
    recentImages: List<String> = emptyList(),
) {
    // Rich-text draft: [initialText] is wire-format (may carry <rt> markup when editing).
    val editor = rememberRichTextEditorController(initialEncoded = initialText)
    val visibleLength = editor.plainText.length
    val canPost = editor.plainText.isNotBlank() && visibleLength <= MAX_TWEET_LENGTH && !state.isPosting
    val scope = rememberCoroutineScope()

    fun picturesUnavailable() {
        scope.launch { snackbarHostState.showSnackbar(IMAGES_UNAVAILABLE) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit tweet" else "New tweet",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(TwitturIcons.Close, contentDescription = "Close") }
                },
                actions = {
                    Button(
                        onClick = { onPost(editor.encode()) },
                        enabled = canPost,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Brand, contentColor = OnBrand),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 12.dp),
                    ) {
                        if (state.isPosting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = OnBrand, strokeWidth = 2.dp)
                        } else {
                            Text(if (isEditing) "Save" else "Post", fontWeight = FontWeight.Bold)
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            // Rich-text editor (vendored RichTextEditor lib) inside the old outlined-field frame.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(width = 1.dp, color = DividerLine, shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                RichTextField(
                    controller = editor,
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    placeholder = "What's happening?",
                    maxLength = MAX_TWEET_LENGTH,
                )
            }

            // The library's floating formatting toolbar (bold/italic/underline/colour/font/size).
            RichTextToolbar(
                controller = editor,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp),
            )

            // Image picker row: a "add image" button + recent device thumbnails (Android only;
            // publishing isn't wired yet, so any tap shows a not-available notice).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandSoft)
                        .clickable { picturesUnavailable() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(TwitturIcons.AddPhoto, contentDescription = "Add image", tint = Brand, modifier = Modifier.size(26.dp))
                }
                recentImages.take(5).forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Recent image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { picturesUnavailable() },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$visibleLength / $MAX_TWEET_LENGTH",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (visibleLength > MAX_TWEET_LENGTH) Danger else SecondaryText,
                )
            }
        }
    }
}
