package com.example.twitturin.auth.presentation.kind.sealed

import com.example.twitturin.auth.presentation.login.sealed.SignInUiEvent

sealed class KindUiEvent {

    data object NavigateToProfReg: KindUiEvent()

    data object NavigateToStudReg: KindUiEvent()

    data object OnBackPressed: KindUiEvent()

    data object StateNoting : KindUiEvent()
}