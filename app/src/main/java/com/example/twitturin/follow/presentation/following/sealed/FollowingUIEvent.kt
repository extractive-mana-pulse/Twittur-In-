package com.example.twitturin.follow.presentation.following.sealed

sealed class FollowingUIEvent {

    data object OnUnFollowPressed: FollowingUIEvent()
    data object OnItemPressed: FollowingUIEvent()
}