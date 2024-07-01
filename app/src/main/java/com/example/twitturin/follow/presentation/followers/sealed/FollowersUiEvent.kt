package com.example.twitturin.follow.presentation.followers.sealed

sealed class FollowersUiEvent {

    data object NothingState: FollowersUiEvent()
    data object OnItemPressed: FollowersUiEvent()
    data object OnFollowPressed: FollowersUiEvent()
}