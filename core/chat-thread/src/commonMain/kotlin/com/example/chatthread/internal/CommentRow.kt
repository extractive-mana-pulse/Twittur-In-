package com.example.chatthread.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.example.chatthread.ThreadComment
import com.example.chatthread.ThreadStyle
import com.example.chatthread.ThreadTag

/**
 * Renders a single visible row inside [com.example.chatthread.CollapsibleChatThread].
 *
 * The outer [Box] applies [threadConnectors] so the tree lines are drawn *behind* the row's
 * content, and reserves [ThreadStyle.rowVerticalSpacing] at the bottom — this padding is inside
 * the row so the connector's own trunk can span it without jumping across a gap to the next row.
 *
 * The inner [Row] is indented by `indentPerLevel * depth`, which places the avatar exactly at the
 * x-coordinate the connector math assumes.
 *
 * @param row Flattened metadata and payload for this row.
 * @param style Shared visual configuration.
 * @param onToggle Invoked when the user taps the expand/collapse chip.
 * @param onReplyClick Invoked when the user taps the reply chip; receives the row's comment.
 */
@Composable
internal fun CommentRow(
    row: VisibleRow,
    style: ThreadStyle,
    onToggle: () -> Unit,
    onReplyClick: (ThreadComment) -> Unit,
) {
    val comment = row.comment

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .threadConnectors(
                depth = row.depth,
                ancestorHasMoreSiblings = row.ancestorHasMoreSiblings,
                isLastSiblingAtDepth = row.isLastSiblingAtDepth,
                isExpanded = row.isExpanded,
                avatarSize = style.avatarSize,
                indentPerLevel = style.indentPerLevel,
                cornerRadius = style.connectorCornerRadius,
                strokeWidth = style.connectorStrokeWidth,
                color = style.connectorColor,
            )
            .padding(bottom = style.rowVerticalSpacing)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = style.indentPerLevel * row.depth),
            verticalAlignment = Alignment.Top,
        ) {
            Avatar(
                initial = comment.avatarInitial,
                background = comment.avatarBackground,
                style = style,
            )
            Spacer(Modifier.width(style.avatarToContentSpacing))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = style.contentPaddingEnd),
            ) {
                AuthorLine(comment = comment, style = style)
                if (comment.tags.isNotEmpty()) {
                    Spacer(Modifier.height(style.contentVerticalGap))
                    TagRow(tags = comment.tags, style = style)
                }
                if (!comment.title.isNullOrEmpty()) {
                    Spacer(Modifier.height(style.contentVerticalGap))
                    Text(
                        text = comment.title,
                        style = style.titleStyle.copy(color = style.primaryTextColor),
                    )
                }
                Spacer(Modifier.height(style.contentVerticalGap))
                Text(
                    text = comment.body,
                    style = style.bodyStyle.copy(color = style.secondaryTextColor),
                )
                Spacer(Modifier.height(style.contentVerticalGap))
                ActionRow(
                    row = row,
                    style = style,
                    onToggle = onToggle,
                    onReplyClick = { onReplyClick(comment) },
                )
            }
        }
    }
}

/**
 * Circular avatar with a single centered initial character.
 *
 * Sized to [ThreadStyle.avatarSize] so the connector math (which derives its column positions from
 * the same value) lines up with the avatar's visual center.
 */
@Composable
private fun Avatar(
    initial: String,
    background: Color,
    style: ThreadStyle,
) {
    Text(
        text = initial,
        style = style.avatarTextStyle.copy(
            color = style.avatarContentColor,
            textAlign = TextAlign.Center,
        ),
        modifier = Modifier
            .size(style.avatarSize)
            .background(background, CircleShape)
            .wrapContentSize(Alignment.Center),
    )
}

/**
 * Renders "Author • timestamp" as a single annotated-string line, plus an optional channel line
 * beneath it.
 *
 * Using [buildAnnotatedString] keeps the two color/size styles on one baseline so the text wraps
 * naturally at narrow widths without being split into two adjacent `Text` composables.
 */
@Composable
private fun AuthorLine(comment: ThreadComment, style: ThreadStyle) {
    val text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = style.primaryTextColor,
                fontSize = style.authorStyle.fontSize,
                fontWeight = style.authorStyle.fontWeight ?: FontWeight.W600,
                letterSpacing = style.authorStyle.letterSpacing,
            )
        ) { append(comment.author) }
        append("  ")
        withStyle(
            SpanStyle(
                color = style.secondaryTextColor,
                fontSize = style.timestampStyle.fontSize,
                fontWeight = style.timestampStyle.fontWeight ?: FontWeight.W500,
                letterSpacing = style.timestampStyle.letterSpacing,
            )
        ) { append("• ${comment.timestamp}") }
    }
    Text(text = text, style = style.authorStyle)

    if (!comment.channel.isNullOrEmpty()) {
        Spacer(Modifier.height(style.contentVerticalGap / 2))
        Text(
            text = comment.channel,
            style = style.channelStyle.copy(color = style.secondaryTextColor),
        )
    }
}

/**
 * Horizontal row of colored pill badges (`ThreadTag`s).
 *
 * Inter-tag spacing reuses [ThreadStyle.contentVerticalGap] so a single style property controls
 * all of the row's micro-spacing.
 */
@Composable
private fun TagRow(tags: List<ThreadTag>, style: ThreadStyle) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        tags.forEachIndexed { index, tag ->
            if (index > 0) Spacer(Modifier.width(style.contentVerticalGap))
            Text(
                text = tag.text,
                style = style.tagStyle.copy(color = tag.contentColor),
                modifier = Modifier
                    .background(tag.background, RoundedCornerShape(style.contentVerticalGap / 2))
                    .padding(
                        horizontal = style.contentVerticalGap,
                        vertical = style.contentVerticalGap / 3,
                    ),
            )
        }
    }
}

/**
 * Bottom-of-row action cluster: [ToggleChip] (only when the row has replies) followed by
 * [ReplyChip].
 */
@Composable
private fun ActionRow(
    row: VisibleRow,
    style: ThreadStyle,
    onToggle: () -> Unit,
    onReplyClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (row.hasReplies) {
            ToggleChip(row = row, style = style, onClick = onToggle)
            Spacer(Modifier.width(style.avatarToContentSpacing))
        }
        ReplyChip(style = style, onClick = onReplyClick)
    }
}

/**
 * Expand/collapse control. Label and icon both flip based on [VisibleRow.isExpanded]; icon is
 * omitted entirely if the caller didn't supply a painter (returns null).
 */
@Composable
private fun ToggleChip(row: VisibleRow, style: ThreadStyle, onClick: () -> Unit) {
    val label = if (row.isExpanded) {
        "Hide replies"
    } else {
        val count = row.comment.replies.size
        if (count == 1) "Show 1 reply" else "Show $count replies"
    }
    val icon = if (row.isExpanded) style.collapseIcon() else style.expandIcon()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        if (icon != null) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(style.avatarSize * 0.7f),
            )
            Spacer(Modifier.width(style.contentVerticalGap))
        }
        Text(
            text = label,
            style = style.actionStyle.copy(color = style.primaryTextColor),
        )
    }
}

/**
 * Reply action. Label comes from [ThreadStyle.replyText] (for localization); icon is optional.
 */
@Composable
private fun ReplyChip(style: ThreadStyle, onClick: () -> Unit) {
    val icon = style.replyIcon()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        if (icon != null) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(style.avatarSize * 0.7f),
            )
            Spacer(Modifier.width(style.contentVerticalGap))
        }
        Text(
            text = style.replyText,
            style = style.actionStyle.copy(color = style.primaryTextColor),
        )
    }
}
