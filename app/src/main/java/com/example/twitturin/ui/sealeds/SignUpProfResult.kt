package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.registration.SignUpProf

sealed class SignUpProfResult {
    data class Success(val response: SignUpProf) : SignUpProfResult()
    data class Error(val message: String) : SignUpProfResult()
}
