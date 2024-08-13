package com.example.twitturin.notification.data.remote.api

import com.example.twitturin.notification.domain.model.MainGit
import retrofit2.Response
import retrofit2.http.GET

interface GithubAPI {

    @GET("https://api.github.com/repos/extractive-mana-pulse/Twittur-In-/releases/latest")
    suspend fun getLatestRelease(): Response<MainGit>
}