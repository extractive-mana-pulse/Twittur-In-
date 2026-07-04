package com.example.twitturin.feature.tweet.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatthread.CollapsibleChatThread
import com.example.chatthread.rememberThreadState
import com.example.richtexteditor.RichTextEditorController
import com.example.richtexteditor.components.RichTextField
import com.example.richtexteditor.components.RichTextToolbar
import com.example.richtexteditor.icons.RichTextIcons
import com.example.richtexteditor.rememberRichText
import com.example.richtexteditor.rememberRichTextEditorController
import com.example.richtexteditor.rememberRichTextFonts
import com.example.twitturin.core.designsystem.component.LoadingBox
import com.example.twitturin.core.designsystem.component.TwitturTopBarMore
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.Danger
import com.example.twitturin.core.designsystem.theme.DividerLine
import com.example.twitturin.core.designsystem.theme.Hint
import com.example.twitturin.core.designsystem.theme.Like
import com.example.twitturin.core.designsystem.theme.OnBrand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.feature.tweet.presentation.TweetUi
import com.example.twitturin.feature.tweet.presentation.components.TweetAvatar
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DetailRoot(
    tweetId: String,
    onBack: () -> Unit,
    onOpenTweet: (String) -> Unit,
    onOpenLikes: (String) -> Unit,
    focusReply: Boolean = false,
    onShare: (String) -> Unit = {},
    onEdit: (tweetId: String, content: String) -> Unit = { _, _ -> },
    viewModel: DetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }
    val replyEditor = rememberRichTextEditorController()

    LaunchedEffect(tweetId) { viewModel.load(tweetId) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is DetailEvent.NavigateToLikes -> onOpenLikes(event.tweetId)
            DetailEvent.Deleted -> onBack()
            DetailEvent.ReplySent -> replyEditor.clear()
            is DetailEvent.ShowError -> pendingError = event.message
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    DetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        focusReply = focusReply,
        onShare = { state.tweet?.let { onShare(it.id) } },
        onEdit = { state.tweet?.let { onEdit(it.id, it.content) } },
        snackbarHostState = snackbarHostState,
        replyEditor = replyEditor,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DetailScreen(
    state: DetailState,
    onAction: (DetailAction) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    focusReply: Boolean = false,
    onShare: () -> Unit = {},
    onEdit: () -> Unit = {},
    replyEditor: RichTextEditorController = rememberRichTextEditorController(),
) {
    var menuOpen by remember { mutableStateOf(false) }
    var showFormatting by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    var focusTick by remember { mutableStateOf(0) }

    // Focus the reply field + raise the keyboard when arriving via a "reply" tap, or the Reply button.
    LaunchedEffect(focusReply, state.tweet != null) {
        if (focusReply && state.tweet != null) focusTick++
    }
    LaunchedEffect(focusTick) {
        if (focusTick > 0) {
            runCatching { focusRequester.requestFocus() }
            keyboard?.show()
        }
    }

    // Aiming at a thread reply (its "Reply" chip) also raises the composer.
    LaunchedEffect(state.replyTarget) {
        if (state.replyTarget != null) focusTick++
    }

    fun report() {
        scope.launch { snackbarHostState.showSnackbar("Thanks — we'll take a look.") }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TwitturTopBarMore(
                title = "Post",
                menuExpanded = menuOpen,
                onMenuClick = { menuOpen = true },
                onMenuDismiss = { menuOpen = false },
                onBack = onBack,
                menu = {
                    if (state.tweet?.isMine == true) {
                        DropdownMenuItem(text = { Text("Edit") }, onClick = { menuOpen = false; onEdit() })
                        DropdownMenuItem(
                            text = { Text("Delete", color = Danger) },
                            onClick = { menuOpen = false; onAction(DetailAction.OnDelete) },
                        )
                    }
                    DropdownMenuItem(text = { Text("Report") }, onClick = { menuOpen = false; report() })
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth(),
            ) {
                // Nested-reply target chip: Send goes under this reply until dismissed.
                state.replyTarget?.let { target ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 12.dp, top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Replying to " +
                                if (target.authorUsername.isNotBlank()) "@${target.authorUsername}" else target.authorName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Brand,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Icon(
                            imageVector = TwitturIcons.Close,
                            contentDescription = "Cancel reply",
                            tint = SecondaryText,
                            modifier = Modifier
                                .height(16.dp)
                                .clickable { onAction(DetailAction.OnCancelReplyTarget) },
                        )
                    }
                }
                // The library's formatting toolbar, summoned by the Aa toggle in the reply row.
                AnimatedVisibility(visible = showFormatting) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        RichTextToolbar(
                            controller = replyEditor,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = RichTextIcons.TextFormat,
                        contentDescription = "Formatting",
                        tint = if (showFormatting) Brand else SecondaryText,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(22.dp)
                            .clickable { showFormatting = !showFormatting },
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(width = 1.dp, color = DividerLine, shape = RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        RichTextField(
                            controller = replyEditor,
                            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                            textStyle = MaterialTheme.typography.bodyLarge,
                            placeholder = "Reply",
                            maxLines = 3,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onAction(DetailAction.OnSendReply(replyEditor.encode())) },
                        enabled = replyEditor.plainText.isNotBlank() && !state.isSendingReply,
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Brand, contentColor = OnBrand),
                    ) { Text("Send", fontWeight = FontWeight.Bold) }
                }
            }
        },
    ) { innerPadding ->
        when {
            state.isLoading && state.tweet == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) { LoadingBox() }
            }

            else -> {
                val richFonts = rememberRichTextFonts()
                val threadComments = remember(state.replies, richFonts) {
                    state.replies.map { it.toThreadComment(richFonts) }
                }
                val repliesById = remember(state.replies) { indexRepliesById(state.replies) }
                val threadState = rememberThreadState()

                // Open the whole tree whenever a fresh reply tree lands (first load + after posting).
                LaunchedEffect(state.replies) {
                    collectExpandableIds(state.replies).forEach(threadState::expand)
                }

                Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    state.tweet?.let { tweet ->
                        DetailHeader(
                            tweet = tweet,
                            // Replying from the header targets the tweet itself, not a thread row.
                            onReply = {
                                onAction(DetailAction.OnCancelReplyTarget)
                                focusTick++
                            },
                            onLike = { onAction(DetailAction.OnLike) },
                            onShare = onShare,
                            onOpenLikes = { onAction(DetailAction.OnOpenLikes) },
                        )
                        HorizontalDivider()
                    }
                    if (threadComments.isEmpty()) {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No replies yet — be the first.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Hint,
                            )
                        }
                    } else {
                        // The reply thread — rendered by the vendored CollapsibleChatThread lib.
                        CollapsibleChatThread(
                            comments = threadComments,
                            state = threadState,
                            style = twitturThreadStyle(),
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 4.dp, bottom = 8.dp),
                            onReplyClick = { comment ->
                                repliesById[comment.id]?.let { onAction(DetailAction.OnReplyToReply(it)) }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHeader(
    tweet: TweetUi,
    onReply: () -> Unit,
    onLike: () -> Unit,
    onShare: () -> Unit,
    onOpenLikes: () -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TweetAvatar(name = tweet.authorName, avatarUrl = tweet.authorAvatar, size = 48.dp)
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = tweet.authorName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "@${tweet.authorUsername}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = rememberRichText(tweet.content), style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = tweet.date, style = MaterialTheme.typography.bodySmall, color = Hint)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "${tweet.likes} likes",
            style = MaterialTheme.typography.bodyMedium,
            color = Brand,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onOpenLikes),
        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = DividerLine)
        Spacer(modifier = Modifier.height(6.dp))

        // X-style action row: reply, like, share.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DetailActionButton(
                icon = TwitturIcons.Reply,
                label = "Reply",
                tint = SecondaryText,
                onClick = onReply,
                modifier = Modifier.weight(1f),
            )
            DetailActionButton(
                icon = if (tweet.isLiked) TwitturIcons.LikeFilled else TwitturIcons.Like,
                label = "Like",
                tint = if (tweet.isLiked) Like else SecondaryText,
                onClick = onLike,
                modifier = Modifier.weight(1f),
            )
            DetailActionButton(
                icon = TwitturIcons.Share,
                label = "Share",
                tint = SecondaryText,
                onClick = onShare,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DetailActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(onClick = onClick).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.height(20.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = tint)
    }
}
