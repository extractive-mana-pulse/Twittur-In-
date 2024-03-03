package com.example.twitturin.model.network

import com.example.twitturin.model.data.ImageResource
import com.example.twitturin.model.data.editUser.EditProfile
import com.example.twitturin.model.data.likeTweet.LikeTweet
import com.example.twitturin.tweet.model.data.Tweet
import com.example.twitturin.model.data.users.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Api  {

    @GET("users")
    suspend fun getAllUsers() : Response<List<User>>

    /** publish user like too  **/
    @POST("tweets/{id}/likes")
    fun postLike(@Body tweet: LikeTweet, @Path("id") userId: String, @Header("Authorization") token: String): Call<LikeTweet>

    @GET("users/{id}/tweets")
    suspend fun getPostsByUser(@Path("id") userId: String): Response<List<Tweet>>

    @FormUrlEncoded
    @PATCH("users/{id}")
    fun updateUserUsername(@Path("id") userId: String, @Header("Authorization") token: String) : Call<EditProfile>

    @GET("users/{id}/likes")
    suspend fun getListOfLikedPosts(@Path("id")userId : String) : Response<List<Tweet>>

    @GET("tweets/{id}/replies")
    suspend fun getRepliesOfPost(@Path("id")tweetId : String) : Response<List<Tweet>>

    @GET("search")
    suspend fun searchNews(@Query("keyword") keyword: Tweet) : Response<Tweet>


    @POST("users/{id}/profilePicture")
    fun loadImage(
        @Body image : ImageResource,
        @Path("id") userId : String,
        @Header("Authorization") token : String
    ) : Call<ImageResource>
}