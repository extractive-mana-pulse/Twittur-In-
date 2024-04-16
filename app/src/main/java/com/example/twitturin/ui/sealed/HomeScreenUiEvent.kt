package com.example.twitturin.ui.sealed

sealed class HomeScreenUiEvent {
    data object NavigateToPublicPost: HomeScreenUiEvent()
    data object OpenDrawer: HomeScreenUiEvent()

}