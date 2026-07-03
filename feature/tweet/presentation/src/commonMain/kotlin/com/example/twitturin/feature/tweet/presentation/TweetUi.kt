package com.example.twitturin.feature.tweet.presentation

import com.example.twitturin.feature.tweet.domain.Tweet

/** Display-ready model for a tweet card. No nullable display fields. */
data class TweetUi(
    val id: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatar: String?,
    val content: String,
    val date: String,
    val likes: Int,
    val replyCount: Int,
    /** Whether the signed-in user authored this tweet (controls the delete/edit affordance). */
    val isMine: Boolean,
    /** Whether the signed-in user has liked this tweet (controls the heart state). */
    val isLiked: Boolean,
    /** The author's id — used to open their profile. */
    val authorId: String,
)

fun Tweet.toTweetUi(currentUserId: String?): TweetUi = TweetUi(
    id = id,
    authorName = author?.fullName ?: "Twittur User",
    authorUsername = author?.username.orEmpty(),
    authorAvatar = author?.profilePicture,
    content = content,
    // The API returns ISO-8601; show just the date part to avoid a date-format dependency.
    date = createdAt?.substringBefore("T").orEmpty(),
    likes = likes,
    replyCount = replyCount,
    isMine = currentUserId != null && author?.id == currentUserId,
    isLiked = currentUserId != null && likedBy.contains(currentUserId),
    authorId = author?.id.orEmpty(),
)
