package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.tweets.Tweet

sealed class UserTweetsResult {
    data class Success(val tweets: List<Tweet>) : UserTweetsResult()
    data class Failure(val message : String) : UserTweetsResult()
}