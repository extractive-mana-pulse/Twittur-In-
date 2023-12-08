package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.registration.SignUpStudent

sealed class SignUpStudentResult {
    data class Success(val response: SignUpStudent) : SignUpStudentResult()
    data class Error(val message: String) : SignUpStudentResult()
}
