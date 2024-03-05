package com.example.twitturin.search.di

import com.example.twitturin.BuildConfig
import com.example.twitturin.profile.model.network.ProfileApi
import com.example.twitturin.search.domain.repository.SearchRepository
import com.example.twitturin.search.domain.repositoryImpl.SearchRepositoryImpl
import com.example.twitturin.search.model.network.SearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideSearchApi(retrofit: Retrofit): SearchApi {
        return retrofit.create(SearchApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(searchApi: SearchApi) : SearchRepository {
        return SearchRepositoryImpl(searchApi)
    }
}