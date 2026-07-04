package com.example.twitturin.feature.tweet.domain

/**
 * One node of a tweet's reply tree (`GET tweets/{id}/replies`).
 *
 * Replies are a separate backend resource from tweets (a reply id 404s on `tweets/{id}`), and
 * they nest: [replies] holds the children to arbitrary depth. Reply-to-reply writes go through
 * `POST replies/{id}`, not the tweets routes.
 */
data class Reply(
    val id: String,
    val content: String,
    val author: TweetAuthor?,
    val createdAt: String?,
    val likes: Int,
    val likedBy: List<String>,
    val isEdited: Boolean,
    val replies: List<Reply>,
)
