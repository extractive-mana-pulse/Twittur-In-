package com.example.twitturin.profile.presentation.sealed

import com.example.twitturin.profile.domain.model.ImageResource

sealed class EditUserImageState {

    data class Success(val result: ImageResource) : EditUserImageState()
    data class Error(val message: String) : EditUserImageState()
    data object Loading: EditUserImageState()
}