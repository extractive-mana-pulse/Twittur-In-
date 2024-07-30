package com.example.twitturin.profile.presentation.sealed

import com.example.twitturin.profile.data.remote.api.photoUrl

sealed class EditUserImageState {

    data class Success(val result: photoUrl) : EditUserImageState()

    data class Error(val message: String) : EditUserImageState()
    data object Loading: EditUserImageState()
}