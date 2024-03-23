package com.example.twitturin.follow.di

import com.example.twitturin.follow.domain.repository.FollowRepository
import com.example.twitturin.follow.domain.repositoryImpl.FollowRepositoryImpl
import com.example.twitturin.follow.presentation.model.network.FollowApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FollowModule {

    @Provides
    @Singleton
    fun provideFollowApi(retrofit: Retrofit): FollowApi {
        return retrofit.create(FollowApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFollowRepository(followApi : FollowApi) : FollowRepository {
        return FollowRepositoryImpl(followApi)
    }
 }