package com.example.twitturin.tweet.presentation.postTweet.sealed

sealed class PostTweetUI {

    data object OnCancelPressed: PostTweetUI()

    data object OnPublishPressed: PostTweetUI()

    data object OnDialogReadPolicyPressed: PostTweetUI()
}