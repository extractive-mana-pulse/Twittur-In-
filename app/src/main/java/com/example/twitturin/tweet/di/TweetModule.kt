package com.example.twitturin.tweet.di

import com.example.twitturin.tweet.domain.repository.TweetRepository
import com.example.twitturin.tweet.domain.repositoryImpl.TweetRepositoryImpl
import com.example.twitturin.tweet.model.network.TweetApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TweetModule {

    @Provides
    @Singleton
    fun provideTweetApi(retrofit: Retrofit) : TweetApi {
        return retrofit.create(TweetApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTweetRepository(tweetApi: TweetApi) : TweetRepository {
        return TweetRepositoryImpl(tweetApi)
    }
}