package com.example.twitturin.feature.tweet.presentation

import com.example.twitturin.feature.tweet.domain.Reply

/** Display-ready node of a tweet's reply tree. No nullable display fields. */
data class ReplyUi(
    val id: String,
    val authorName: String,
    val authorUsername: String,
    val content: String,
    val date: String,
    val replies: List<ReplyUi>,
)

fun Reply.toReplyUi(): ReplyUi = ReplyUi(
    id = id,
    authorName = author?.fullName ?: "Twittur User",
    authorUsername = author?.username.orEmpty(),
    content = content,
    // The API returns ISO-8601; show just the date part to avoid a date-format dependency.
    date = createdAt?.substringBefore("T").orEmpty(),
    replies = replies.map { it.toReplyUi() },
)
