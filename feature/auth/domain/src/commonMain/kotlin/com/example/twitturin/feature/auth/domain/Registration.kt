package com.example.twitturin.feature.auth.domain

/** Sign-up payload for a student. `kind` ("student") is applied in the data layer. */
data class StudentRegistration(
    val fullName: String,
    val username: String,
    val studentId: String,
    val major: String,
    val password: String,
)

/** Sign-up payload for a professor. `kind` ("teacher") is applied in the data layer. */
data class ProfessorRegistration(
    val fullName: String,
    val username: String,
    val subject: String,
    val password: String,
)
