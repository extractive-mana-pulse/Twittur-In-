package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.publicTweet.TweetContent

sealed class EditTweetResult {

    data class Success(val editUser : TweetContent) : EditTweetResult()
    data class Error(val statusCode: Int) : EditTweetResult() {
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