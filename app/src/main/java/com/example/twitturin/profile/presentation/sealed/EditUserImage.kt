package com.example.twitturin.profile.presentation.sealed

import com.example.twitturin.profile.domain.model.ImageResource

sealed class EditUserImage {

    data class Success(val editUserImage: ImageResource) : EditUserImage()

    data class Error(val error: String) : EditUserImage()
}