package com.example.twitturin.detail.di

import com.example.twitturin.detail.data.remote.api.ListOfLikesApi
import com.example.twitturin.detail.data.remote.repository.ListOfLikesRepository
import com.example.twitturin.detail.domain.repositoryImpl.ListOfLikesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DetailModule {

    @Provides
    @Singleton
    fun provideLolApi(retrofit: Retrofit): ListOfLikesApi {
        return retrofit.create(ListOfLikesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLolRepository(lolApi: ListOfLikesApi) : ListOfLikesRepository {
        return ListOfLikesRepositoryImpl(lolApi)
    }
}