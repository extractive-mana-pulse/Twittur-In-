package com.example.twitturin.model.repo

import com.example.twitturin.tweet.model.data.Tweet
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.network.RetrofitInstance
import retrofit2.Response

class Repository {

//    override suspend fun doNetworkCall() {
        // TODO
//    }

    suspend fun getAllUsers(): Response<List<User>> {
        return RetrofitInstance.api.getAllUsers()
    }

    suspend fun getTweets() : Response<List<Tweet>> {
        return RetrofitInstance.tweetApi.getTweet()
    }

    suspend fun getUserTweets(userId: String) : Response<List<Tweet>> {
        return RetrofitInstance.api.getPostsByUser(userId)
    }

    suspend fun getFollowersList(userId: String) : Response<List<User>> {
        return RetrofitInstance.followApi.getListOfFollowers(userId)
    }

    suspend fun getFollowingList(userId: String) : Response<List<User>> {
        return RetrofitInstance.followApi.getListOfFollowing(userId)
    }

    suspend fun getListOfLikedPosts(userId: String) : Response<List<Tweet>> {
        return RetrofitInstance.api.getListOfLikedPosts(userId)
    }

    suspend fun getRepliesOfTweet(tweetId: String) : Response<List<Tweet>> {
        return RetrofitInstance.api.getRepliesOfPost(tweetId)
    }

    suspend fun searchNews(searchQuery: Tweet) = RetrofitInstance.api.searchNews(searchQuery)
}