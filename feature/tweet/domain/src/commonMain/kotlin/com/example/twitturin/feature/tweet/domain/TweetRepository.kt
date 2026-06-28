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
}
