package com.example.twitturin.notification.di

import com.example.twitturin.notification.data.remote.api.GithubAPI
import com.example.twitturin.notification.data.remote.repository.GithubRepository
import com.example.twitturin.notification.domain.repositoryImpl.GithubRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GitModule {

    @Provides
    @Singleton
    fun provideGitApi(retrofit: Retrofit): GithubAPI { return retrofit.create(GithubAPI::class.java) }

    @Provides
    @Singleton
    fun provideGithubRepository(githubAPI: GithubAPI) : GithubRepository { return GithubRepositoryImpl(githubAPI) }
}