package com.example.twitturin.presentation.api

import com.example.twitturin.presentation.data.SignIn
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Api  {
    @POST("auth")
    fun authUser(@Body authUSer: SignIn): Call<SignIn>
}