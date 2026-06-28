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

    /** Publish a new tweet (`POST tweets`). */
    suspend fun postTweet(content: String): EmptyResult<DataError.Network>

    /** Delete a tweet the current user owns (`DELETE tweets/{id}`). */
    suspend fun deleteTweet(tweetId: String): EmptyResult<DataError.Network>

    // --- detail view (a tweet + its replies + who liked it) ---

    /** A single tweet by id (`GET tweets/{id}`). */
    suspend fun getTweet(tweetId: String): Result<Tweet, DataError.Network>

    /** Replies to a tweet (`GET tweets/{id}/replies`); replies are themselves tweet-shaped. */
    suspend fun getReplies(tweetId: String): Result<List<Tweet>, DataError.Network>

    /** Reply to a tweet (`POST tweets/{id}/replies`, auth). */
    suspend fun postReply(tweetId: String, content: String): EmptyResult<DataError.Network>

    /** Users who liked a tweet (`GET tweets/{id}/likes`). */
    suspend fun getLikers(tweetId: String): Result<List<TweetLiker>, DataError.Network>
}
