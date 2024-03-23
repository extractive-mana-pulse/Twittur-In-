package com.example.twitturin.auth.presentation.sealed

import com.example.twitturin.auth.presentation.model.data.SignUpStudent

sealed class SignUpStudentResult {
    data class Success(val response: SignUpStudent) : SignUpStudentResult()
    data class Error(val message: String) : SignUpStudentResult()
}
