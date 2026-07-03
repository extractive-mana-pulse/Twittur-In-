package com.example.twitturin.feature.tweet.presentation.detail

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chatthread.ThreadComment
import com.example.chatthread.ThreadStyle
import com.example.twitturin.core.designsystem.theme.AvatarGradients
import com.example.twitturin.core.designsystem.theme.Hint
import com.example.twitturin.core.designsystem.theme.OnBrand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.feature.tweet.presentation.ReplyUi

/**
 * Bridges the reply tree to the vendored CollapsibleChatThread library (:core:chat-thread).
 * [ThreadComment.id] is the backend reply id, so thread expansion state and reply targeting
 * both key off the real resource.
 */
internal fun ReplyUi.toThreadComment(): ThreadComment = ThreadComment(
    id = id,
    author = authorName,
    avatarBackground = avatarColorFor(authorName),
    timestamp = date,
    body = content,
    avatarInitial = authorName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?",
    channel = if (authorUsername.isBlank()) null else "@$authorUsername",
    replies = replies.map { it.toThreadComment() },
)

/** Every reply id that has children — used to open the whole tree once replies load. */
internal fun collectExpandableIds(replies: List<ReplyUi>): Set<String> {
    val ids = mutableSetOf<String>()
    fun walk(nodes: List<ReplyUi>) {
        nodes.forEach { node ->
            if (node.replies.isNotEmpty()) {
                ids.add(node.id)
                walk(node.replies)
            }
        }
    }
    walk(replies)
    return ids
}

/** Flattens the tree into an id-indexed map so a tapped [ThreadComment] maps back to its [ReplyUi]. */
internal fun indexRepliesById(replies: List<ReplyUi>): Map<String, ReplyUi> {
    val index = mutableMapOf<String, ReplyUi>()
    fun walk(nodes: List<ReplyUi>) {
        nodes.forEach { node ->
            index[node.id] = node
            walk(node.replies)
        }
    }
    walk(replies)
    return index
}

/** Solid avatar color: the first stop of the same gradient GradientAvatar picks for this seed. */
private fun avatarColorFor(seed: String): Color {
    val idx = if (seed.isEmpty()) 0 else (seed.sumOf { it.code } % AvatarGradients.size)
    return AvatarGradients[idx].first()
}

/** [ThreadStyle] re-skinned with the TwitturIn design system (DM Sans type + brand palette). */
@Composable
internal fun twitturThreadStyle(): ThreadStyle = ThreadStyle.Default.copy(
    avatarSize = 32.dp,
    avatarContentColor = OnBrand,
    connectorColor = Hint.copy(alpha = 0.5f),
    connectorStrokeWidth = 1.5.dp,
    connectorCornerRadius = 10.dp,
    indentPerLevel = 24.dp,
    rowVerticalSpacing = 14.dp,
    primaryTextColor = MaterialTheme.colorScheme.onBackground,
    secondaryTextColor = SecondaryText,
    authorStyle = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
    timestampStyle = MaterialTheme.typography.bodySmall,
    channelStyle = MaterialTheme.typography.bodySmall,
    bodyStyle = MaterialTheme.typography.bodyMedium,
    actionStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
    avatarTextStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
)
