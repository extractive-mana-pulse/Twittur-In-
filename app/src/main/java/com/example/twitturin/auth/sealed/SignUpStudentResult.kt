package com.example.twitturin.auth.sealed

import com.example.twitturin.auth.model.data.SignUpStudent

sealed class SignUpStudentResult {
    data class Success(val response: SignUpStudent) : SignUpStudentResult()
    data class Error(val message: String) : SignUpStudentResult()
}
