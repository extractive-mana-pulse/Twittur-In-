package com.example.twitturin.feature.tweet.presentation.di

import com.example.twitturin.feature.tweet.presentation.detail.DetailViewModel
import com.example.twitturin.feature.tweet.presentation.feed.FeedViewModel
import com.example.twitturin.feature.tweet.presentation.likes.LikesListViewModel
import com.example.twitturin.feature.tweet.presentation.post.PostTweetViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tweetPresentationModule = module {
    viewModelOf(::FeedViewModel)
    viewModelOf(::PostTweetViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::LikesListViewModel)
}
