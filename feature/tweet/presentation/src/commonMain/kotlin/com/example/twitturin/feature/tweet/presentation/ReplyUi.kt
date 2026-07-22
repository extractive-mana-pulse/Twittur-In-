package com.example.twitturin.feature.tweet.presentation

import com.example.twitturin.feature.tweet.domain.Reply

/** Display-ready node of a tweet's reply tree. No nullable display fields. */
data class ReplyUi(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val content: String,
    val date: String,
    val likes: Int,
    /** Whether the signed-in user has liked this reply (drives the heart in the thread row). */
    val isLiked: Boolean,
    /** Whether the signed-in user authored this reply (shows the edit/delete actions). */
    val isMine: Boolean,
    val replies: List<ReplyUi>,
)

fun Reply.toReplyUi(currentUserId: String?): ReplyUi = ReplyUi(
    id = id,
    authorId = author?.id.orEmpty(),
    authorName = author?.fullName ?: "Twittur User",
    authorUsername = author?.username.orEmpty(),
    content = content,
    // The API returns ISO-8601; show just the date part to avoid a date-format dependency.
    date = createdAt?.substringBefore("T").orEmpty(),
    likes = likes,
    isLiked = currentUserId != null && likedBy.contains(currentUserId),
    isMine = currentUserId != null && author?.id == currentUserId,
    replies = replies.map { it.toReplyUi(currentUserId) },
)
