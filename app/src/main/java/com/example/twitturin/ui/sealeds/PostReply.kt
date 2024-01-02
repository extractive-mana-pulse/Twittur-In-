package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.replyToTweet.ReplyContent


sealed class PostReply {
    data class Success(val response: ReplyContent) : PostReply()
    data class Error(val message: String) : PostReply()
}