package com.example.twitturin.ui.sealeds

sealed class FollowResult {
    data object Success : FollowResult()
    data class Error(val message: String) : FollowResult()
}