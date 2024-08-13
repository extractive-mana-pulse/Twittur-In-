package com.example.twitturin.notification.domain.repositoryImpl

import com.example.twitturin.notification.data.remote.api.GithubAPI
import com.example.twitturin.notification.data.remote.repository.GithubRepository
import com.example.twitturin.notification.domain.model.MainGit
import retrofit2.Response

class GithubRepositoryImpl(private val githubAPI: GithubAPI): GithubRepository {
    override suspend fun getLatestRelease(): Response<MainGit> = githubAPI.getLatestRelease()
}