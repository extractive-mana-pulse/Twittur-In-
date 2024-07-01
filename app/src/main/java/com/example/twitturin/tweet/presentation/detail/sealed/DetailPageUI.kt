package com.example.twitturin.tweet.presentation.detail.sealed

sealed class DetailPageUI {

    data object OnBackPressed : DetailPageUI()

    data object OnFollowPressed : DetailPageUI()

    data object OnMorePressed : DetailPageUI()

    data object OnListOfLikesPressed : DetailPageUI()

    data object OnCommentPressed : DetailPageUI()

    data object OnLikePressed : DetailPageUI()

    data object OnSharePressed : DetailPageUI()

    data object OnSendReplyPressed : DetailPageUI()

    data object OnImagePressed : DetailPageUI()
}