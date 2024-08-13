package com.example.twitturin.notification.data.remote.repository

import com.example.twitturin.notification.domain.model.MainGit
import retrofit2.Response

interface GithubRepository {
    suspend fun getLatestRelease(): Response<MainGit>
}