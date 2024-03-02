package com.example.twitturin.tweet.di

import com.example.twitturin.BuildConfig
import com.example.twitturin.tweet.model.network.TweetApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn
object TweetModule {

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideTweetApi(retrofit: Retrofit) : TweetApi {
        return retrofit.create(TweetApi::class.java)
    }
}