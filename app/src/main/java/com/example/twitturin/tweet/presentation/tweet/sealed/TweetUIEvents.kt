package com.example.twitturin.tweet.presentation.tweet.sealed

sealed class TweetUIEvents {

    data object OnItemPressed: TweetUIEvents()
    data object OnReplyPressed: TweetUIEvents()
    data object OnHeartPressed: TweetUIEvents()
    data object OnMorePressed: TweetUIEvents()
    data object OnSharePressed: TweetUIEvents()

}