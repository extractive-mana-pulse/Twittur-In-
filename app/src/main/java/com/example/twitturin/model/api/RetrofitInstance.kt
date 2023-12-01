package com.example.twitturin.model.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: com.example.twitturin.model.api.Api by lazy {
        Retrofit.Builder()
            .baseUrl("https://twitturin.onrender.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(com.example.twitturin.model.api.Api::class.java)
    }
}