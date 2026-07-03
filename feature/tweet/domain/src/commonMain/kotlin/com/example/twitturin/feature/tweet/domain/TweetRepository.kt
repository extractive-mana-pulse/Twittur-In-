package com.example.twitturin.feature.tweet.domain

import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult
import com.example.twitturin.core.domain.util.Result

/**
 * Contract for reading and writing tweets. Implemented in :feature:tweet:data over Ktor.
 * Writes (post/delete) require the bearer token, which the Ktor client attaches automatically
 * for the twitturin host from [com.example.twitturin.core.domain.auth.SessionSource].
 */
interface TweetRepository {
    /** The public home feed (`GET tweets`). */
    suspend fun getFeed(): Result<List<Tweet>, DataError.Network>

    /** Tweets authored by a given user (`GET users/{id}/tweets`). */
    suspend fun getUserTweets(userId: String): Result<List<Tweet>, DataError.Network>

    /** Replies authored by a given user (`GET users/{id}/replies`). */
    suspend fun getUserReplies(userId: String): Result<List<Tweet>, DataError.Network>

    /** Tweets a given user has liked (`GET users/{id}/likes`). */
    suspend fun getUserLikes(userId: String): Result<List<Tweet>, DataError.Network>

    /** Publish a new tweet (`POST tweets`). */
    suspend fun postTweet(content: String): EmptyResult<DataError.Network>

    /** Delete a tweet the current user owns (`DELETE tweets/{id}`). */
    suspend fun deleteTweet(tweetId: String): EmptyResult<DataError.Network>

    // --- detail view (a tweet + its replies + who liked it) ---

    /** A single tweet by id (`GET tweets/{id}`). */
    suspend fun getTweet(tweetId: String): Result<Tweet, DataError.Network>

    /** The reply tree of a tweet (`GET tweets/{id}/replies`); each [Reply] nests its children. */
    suspend fun getReplies(tweetId: String): Result<List<Reply>, DataError.Network>

    /** Reply to a tweet (`POST tweets/{id}/replies`, auth). */
    suspend fun postReply(tweetId: String, content: String): EmptyResult<DataError.Network>

    /** Reply to another reply (`POST replies/{id}`, auth) — nests under it in the reply tree. */
    suspend fun postReplyToReply(replyId: String, content: String): EmptyResult<DataError.Network>

    /** Users who liked a tweet (`GET tweets/{id}/likes`). */
    suspend fun getLikers(tweetId: String): Result<List<TweetLiker>, DataError.Network>

    // --- interactions ---

    /** Like a tweet (`POST tweets/{id}/likes`, auth); [newCount] is the resulting like total. */
    suspend fun likeTweet(tweetId: String, newCount: Int): EmptyResult<DataError.Network>

    /** Remove a like (`DELETE tweets/{id}/likes`, auth); [newCount] is the resulting like total. */
    suspend fun unlikeTweet(tweetId: String, newCount: Int): EmptyResult<DataError.Network>

    /** Edit a tweet the current user owns (`PUT tweets/{id}`, auth). */
    suspend fun editTweet(tweetId: String, content: String): EmptyResult<DataError.Network>
}
