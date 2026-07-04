package com.example.twitturin.feature.tweet.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.richtexteditor.rememberRichText
import com.example.twitturin.core.designsystem.component.GradientAvatar
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.Like
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.feature.tweet.presentation.TweetUi

/** Posts taller than this are collapsed behind an expandable "Show more". */
private val CollapsedContentMaxHeight = 200.dp

/**
 * A single tweet card — shared by the home feed, profile tabs, likes and replies.
 * Avatar falls back to an initial-on-gradient when there's no photo. Reply / like / share are
 * tappable: the heart fills + turns [Like] pink when the signed-in user has liked it.
 * [headerAction] hosts a trailing control (unused by the feed now that delete moved to detail).
 */
@Composable
fun TweetItem(
    tweet: TweetUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onReply: () -> Unit = {},
    onLike: () -> Unit = {},
    onShare: () -> Unit = {},
    headerAction: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            TweetAvatar(name = tweet.authorName, avatarUrl = tweet.authorAvatar, size = 44.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tweet.authorName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "@${tweet.authorUsername} · ${tweet.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            headerAction()
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content is wire-format rich text; unformatted tweets pass through unchanged.
        // Tall posts collapse to [CollapsedContentMaxHeight] behind a Show more / Show less toggle.
        var expanded by rememberSaveable(tweet.id) { mutableStateOf(false) }
        var overflows by remember(tweet.id) { mutableStateOf(false) }
        Text(
            text = rememberRichText(tweet.content),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .then(if (expanded) Modifier else Modifier.heightIn(max = CollapsedContentMaxHeight)),
            onTextLayout = { if (!expanded) overflows = it.hasVisualOverflow },
        )
        if (overflows || expanded) {
            Text(
                text = if (expanded) "Show less" else "Show more",
                style = MaterialTheme.typography.bodyMedium,
                color = Brand,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickable { expanded = !expanded },
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            EngagementStat(icon = TwitturIcons.Reply, count = tweet.replyCount, onClick = onReply)
            Spacer(modifier = Modifier.width(28.dp))
            EngagementStat(
                icon = if (tweet.isLiked) TwitturIcons.LikeFilled else TwitturIcons.Like,
                count = tweet.likes,
                onClick = onLike,
                tint = if (tweet.isLiked) Like else SecondaryText,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = TwitturIcons.Share,
                contentDescription = "Share",
                tint = SecondaryText,
                modifier = Modifier.size(18.dp).clickable(onClick = onShare),
            )
        }
    }
}

/** Circular avatar: initial-on-gradient fallback with the photo layered on top when present. */
@Composable
fun TweetAvatar(
    name: String,
    avatarUrl: String?,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(size)) {
        GradientAvatar(name = name, size = size)
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier.size(size).clip(CircleShape),
            )
        }
    }
}

@Composable
private fun EngagementStat(
    icon: ImageVector,
    count: Int,
    onClick: () -> Unit,
    tint: Color = SecondaryText,
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        Text(count.toString(), style = MaterialTheme.typography.bodySmall, color = tint)
    }
}
