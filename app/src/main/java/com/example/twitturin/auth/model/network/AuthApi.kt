package com.example.twitturin.auth.model.network

import com.example.twitturin.auth.model.data.Login
import com.example.twitturin.auth.model.data.SignUpProf
import com.example.twitturin.auth.model.data.SignUpStudent
import com.example.twitturin.model.data.users.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    /** login **/
    @POST("auth")
    fun signInUser(@Body authUSer: Login): Call<User>

    /** professor registration **/
    @POST("users")
    fun signUpProf(@Body user: SignUpProf): Call<SignUpProf>

    /** student registration **/
    @POST("users")
    fun signUpStudent(@Body user: SignUpStudent): Call<SignUpStudent>

}