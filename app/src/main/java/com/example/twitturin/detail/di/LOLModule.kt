package com.example.twitturin.detail.di

import com.example.twitturin.detail.data.remote.api.LOLApi
import com.example.twitturin.detail.data.remote.repository.LOLRepository
import com.example.twitturin.detail.domain.repositoryImpl.LOLRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LOLModule {

    @Provides
    @Singleton
    fun provideLolApi(retrofit: Retrofit): LOLApi {
        return retrofit.create(LOLApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLolRepository(lolApi: LOLApi) : LOLRepository {
        return LOLRepositoryImpl(lolApi)
    }
}