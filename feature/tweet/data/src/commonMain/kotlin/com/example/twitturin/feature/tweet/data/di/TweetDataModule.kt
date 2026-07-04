package com.example.twitturin.feature.tweet.data.di

import com.example.twitturin.feature.tweet.data.TweetRepositoryImpl
import com.example.twitturin.feature.tweet.domain.TweetRepository
import org.koin.dsl.module

val tweetDataModule = module {
    single<TweetRepository> { TweetRepositoryImpl(httpClient = get()) }
}
