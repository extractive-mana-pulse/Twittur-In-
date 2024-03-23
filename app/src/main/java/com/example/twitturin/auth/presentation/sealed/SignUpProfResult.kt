package com.example.twitturin.auth.presentation.sealed

import com.example.twitturin.auth.presentation.model.data.SignUpProf

sealed class SignUpProfResult {
    data class Success(val response: SignUpProf) : SignUpProfResult()
    data class Error(val message: String) : SignUpProfResult()
}
