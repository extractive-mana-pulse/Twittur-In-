package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.publicTweet.TweetContent

sealed class PostTweet{
    data class Success(val response: TweetContent) : PostTweet()
    data class Error(val message: String) : PostTweet()
}
