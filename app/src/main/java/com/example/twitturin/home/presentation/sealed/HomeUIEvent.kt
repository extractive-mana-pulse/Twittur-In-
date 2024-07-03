package com.example.twitturin.home.presentation.sealed

sealed class HomeUIEvent {

    data object OpenDrawer: HomeUIEvent()

    data object NavigateToPublicPost: HomeUIEvent()

}