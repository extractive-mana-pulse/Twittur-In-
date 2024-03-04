package com.example.twitturin.auth.model.domain.repositoryImpl

import com.example.twitturin.auth.model.data.Login
import com.example.twitturin.auth.model.data.SignUpProf
import com.example.twitturin.auth.model.data.SignUpStudent
import com.example.twitturin.auth.model.domain.repository.AuthRepository
import com.example.twitturin.auth.model.network.AuthApi
import com.example.twitturin.auth.model.data.User
import retrofit2.Call

class AuthRepositoryImpl(private val authApi: AuthApi) : AuthRepository {
    override fun signInUser(authUser: Login): Call<User> {
        return authApi.signInUser(authUser)
    }

    override fun signUpProf(user: SignUpProf): Call<SignUpProf> {
        return authApi.signUpProf(user)
    }

    override fun signUpStudent(user: SignUpStudent): Call<SignUpStudent> {
        return authApi.signUpStudent(user)
    }

}