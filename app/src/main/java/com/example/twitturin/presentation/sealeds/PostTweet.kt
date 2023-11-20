package com.example.twitturin.presentation.sealeds

import com.example.twitturin.presentation.data.publicTweet.TheTweet

sealed class PostTweet{
    data class Success(val response: TheTweet) : PostTweet()
    data class Error(val message: String) : PostTweet()
}
