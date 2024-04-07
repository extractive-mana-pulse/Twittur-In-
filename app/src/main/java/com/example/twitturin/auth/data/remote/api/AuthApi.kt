package com.example.twitturin.auth.data.remote.api

import com.example.twitturin.auth.domain.model.AuthUser
import com.example.twitturin.auth.domain.model.Login
import com.example.twitturin.auth.domain.model.SignUpProf
import com.example.twitturin.auth.domain.model.SignUpStudent
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth")
    fun signInUser(@Body authUser: Login): Call<AuthUser>

    @POST("users")
    fun signUpProf(@Body user: SignUpProf): Call<SignUpProf>

    @POST("users")
    fun signUpStudent(@Body user: SignUpStudent): Call<SignUpStudent>

}