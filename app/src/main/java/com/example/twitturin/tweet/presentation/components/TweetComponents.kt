package com.example.twitturin.tweet.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.twitturin.R
import com.example.twitturin.core.extensions.formatCreatedAtPost
import com.example.twitturin.tweet.domain.model.Tweet

/** A single tweet card — shared by the home feed, profile tweets, likes, and replies lists. */
@Composable
fun TweetItem(
    tweet: Tweet,
    onClick: () -> Unit,
    onHeart: () -> Unit,
    onReply: () -> Unit,
    onShare: () -> Unit,
    headerAction: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = tweet.author?.profilePicture,
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tweet.author?.fullName ?: stringResource(R.string.default_user_fullname),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "@${tweet.author?.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = tweet.createdAt?.formatCreatedAtPost().orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            headerAction()
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = tweet.content.orEmpty(),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TweetAction(icon = Icons.Filled.FavoriteBorder, count = tweet.likes, onClick = onHeart)
            TweetAction(icon = Icons.AutoMirrored.Filled.Comment, count = tweet.replyCount, onClick = onReply)
            IconButton(onClick = onShare) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = null)
            }
        }
    }
}

@Composable
private fun TweetAction(icon: ImageVector, count: Int?, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = null)
        }
        Text(text = (count ?: 0).toString(), style = MaterialTheme.typography.bodySmall)
    }
}
