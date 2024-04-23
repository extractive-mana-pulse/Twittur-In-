package com.example.twitturin.tweet.presentation.home.sealed

sealed class HomeScreenUiEvent {
    data object NavigateToPublicPost: HomeScreenUiEvent()
    data object OpenDrawer: HomeScreenUiEvent()

}