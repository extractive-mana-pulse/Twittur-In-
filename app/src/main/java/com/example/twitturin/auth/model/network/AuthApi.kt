package com.example.twitturin.auth.model.network

import com.example.twitturin.auth.model.data.Login
import com.example.twitturin.auth.model.data.SignUpProf
import com.example.twitturin.auth.model.data.SignUpStudent
import com.example.twitturin.auth.model.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth")
    fun signInUser(@Body authUser: Login): Call<User>

    @POST("users")
    fun signUpProf(@Body user: SignUpProf): Call<SignUpProf>

    @POST("users")
    fun signUpStudent(@Body user: SignUpStudent): Call<SignUpStudent>

}