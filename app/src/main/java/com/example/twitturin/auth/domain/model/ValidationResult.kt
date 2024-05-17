package com.example.twitturin.auth.domain.model

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)