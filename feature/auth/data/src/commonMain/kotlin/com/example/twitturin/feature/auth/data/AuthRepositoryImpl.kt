package com.example.twitturin.feature.auth.data

import com.example.twitturin.core.data.network.post
import com.example.twitturin.core.domain.auth.SessionSource
import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult
import com.example.twitturin.core.domain.util.asEmptyResult
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.feature.auth.domain.AuthRepository
import com.example.twitturin.feature.auth.domain.ProfessorRegistration
import com.example.twitturin.feature.auth.domain.StudentRegistration
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionSource: SessionSource,
) : AuthRepository {

    override suspend fun login(username: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<LoginRequestDto, AuthUserDto>(
            route = "auth",
            body = LoginRequestDto(username = username, password = password),
        ).onSuccess { auth ->
            sessionSource.setToken(auth.token)
            sessionSource.setUserId(auth.id)
        }.asEmptyResult()
    }

    override suspend fun registerStudent(student: StudentRegistration): EmptyResult<DataError.Network> {
        // Response type is Unit: the server echoes back the created user WITHOUT `password`,
        // so deserializing into the request DTO would throw MissingFieldException even on a
        // successful 2xx. We only need success/failure here, so discard the body.
        return httpClient.post<StudentRegistrationDto, Unit>(
            route = "users",
            body = StudentRegistrationDto(
                fullName = student.fullName,
                username = student.username,
                studentId = student.studentId,
                major = student.major,
                password = student.password,
                kind = "student",
            ),
        )
    }

    override suspend fun registerProfessor(professor: ProfessorRegistration): EmptyResult<DataError.Network> {
        return httpClient.post<ProfessorRegistrationDto, Unit>(
            route = "users",
            body = ProfessorRegistrationDto(
                fullName = professor.fullName,
                username = professor.username,
                subject = professor.subject,
                password = professor.password,
                kind = "teacher",
            ),
        )
    }

    override fun setRemembered(remembered: Boolean) {
        sessionSource.setRemembered(remembered)
    }
}
