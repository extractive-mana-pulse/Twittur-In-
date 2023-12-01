package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.publicTweet.TheTweet

sealed class PostTweet{
    data class Success(val response: TheTweet) : PostTweet()
    data class Error(val message: String) : PostTweet()
}
