package com.example.twitturin.tweet.presentation.sealed

import com.example.twitturin.tweet.data.data.TweetContent

sealed class EditTweet {

    data class Success(val editUser : TweetContent) : EditTweet()
    data class Error(val statusCode: Int) : EditTweet() {
        val error: String
            get() = when (statusCode) {
                400 -> "Bad Request"
                401 -> "Unauthorized"
                403 -> "Forbidden"
                404 -> "Not Found"
                500 -> "Internal Server Error"
                else -> "Unknown Error"
            }
    }
}