package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.registration.SignUp

sealed class SignUpResult {
    data class Success(val response: SignUp) : SignUpResult()
    data class Error(val message: String) : SignUpResult()
}
