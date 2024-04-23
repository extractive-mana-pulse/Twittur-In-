package com.example.twitturin.auth.presentation.kind.sealed

sealed class KindUiEvent {

    data object NavigateToProfReg: KindUiEvent()

    data object NavigateToStudReg: KindUiEvent()

    data object OnBackPressedFromKindPage: KindUiEvent()
}