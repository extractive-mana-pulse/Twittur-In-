package com.example.twitturin.tweet.presentation.sealed

import com.example.twitturin.tweet.data.data.ReplyContent


sealed class PostReply {
    data class Success(val response: ReplyContent) : PostReply()
    data class Error(val message: String) : PostReply()
}