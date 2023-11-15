package com.example.twitturin.presentation.sealeds

import com.example.twitturin.presentation.data.registration.SignUp

sealed class SignUpResult {
    data class Success(val response: SignUp) : SignUpResult()
    data class Error(val message: String) : SignUpResult()
}
