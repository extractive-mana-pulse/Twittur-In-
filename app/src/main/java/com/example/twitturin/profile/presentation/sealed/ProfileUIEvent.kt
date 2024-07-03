package com.example.twitturin.profile.presentation.sealed

sealed class ProfileUIEvent {

    data object OnBackPressed : ProfileUIEvent()

    data object OnAvatarPressed : ProfileUIEvent()

    data object OnFollowersPressed : ProfileUIEvent()

    data object OnFollowingPressed : ProfileUIEvent()
}