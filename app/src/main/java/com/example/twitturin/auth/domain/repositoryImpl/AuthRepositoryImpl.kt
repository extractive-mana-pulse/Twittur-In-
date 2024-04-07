package com.example.twitturin.auth.domain.repositoryImpl

import com.example.twitturin.auth.data.remote.api.AuthApi
import com.example.twitturin.auth.data.remote.repository.AuthRepository
import com.example.twitturin.auth.domain.model.AuthUser
import com.example.twitturin.auth.domain.model.Login
import com.example.twitturin.auth.domain.model.SignUpProf
import com.example.twitturin.auth.domain.model.SignUpStudent
import retrofit2.Call

class AuthRepositoryImpl(private val authApi: AuthApi) : AuthRepository {

    override fun signInUser(authUser: Login): Call<AuthUser> {
        return authApi.signInUser(authUser)
    }

    override fun signUpProf(user: SignUpProf): Call<SignUpProf> {
        return authApi.signUpProf(user)
    }

    override fun signUpStudent(user: SignUpStudent): Call<SignUpStudent> {
        return authApi.signUpStudent(user)
    }

}