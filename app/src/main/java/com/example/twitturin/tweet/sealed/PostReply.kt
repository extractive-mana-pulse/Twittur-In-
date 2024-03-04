package com.example.twitturin.tweet.sealed

import com.example.twitturin.tweet.model.data.ReplyContent


sealed class PostReply {
    data class Success(val response: ReplyContent) : PostReply()
    data class Error(val message: String) : PostReply()
}