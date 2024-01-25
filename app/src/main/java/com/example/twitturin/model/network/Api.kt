package com.example.twitturin.model.network

import com.example.twitturin.model.data.ImageResource
import com.example.twitturin.model.data.editUser.EditProfile
import com.example.twitturin.model.data.likeTweet.LikeTweet
import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.data.registration.Login
import com.example.twitturin.model.data.registration.SignUpProf
import com.example.twitturin.model.data.registration.SignUpStudent
import com.example.twitturin.model.data.replyToTweet.ReplyContent
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.data.users.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface Api  {

    @GET("users")
    suspend fun getAllUsers() : Response<List<User>>

    /** delete account **/
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: String, @Header("Authorization") token: String): Response<Unit>

    @DELETE("tweets/{id}")
    suspend fun deleteTweet(@Path("id") tweetId: String, @Header("Authorization") token: String): Response<Unit>

    /** publish user like too  **/
    @POST("tweets/{id}/likes")
    fun postLike(@Body tweet: LikeTweet, @Path("id") userId: String, @Header("Authorization") token: String): Call<LikeTweet>

    /** login **/
    @POST("auth")
    fun signInUser(@Body authUSer: Login): Call<User>

    /** professor registration **/
    @POST("users")
    fun signUpProf(@Body user: SignUpProf): Call<SignUpProf>

    /** student registration **/
    @POST("users")
    fun signUpStudent(@Body user: SignUpStudent): Call<SignUpStudent>

    /** In this API call we are publishing post for user! **/
    @POST("tweets")
    fun postTweet(@Body tweet: TweetContent, @Header("Authorization") token: String): Call<TweetContent>

    /** In this API call we getting all posts available from all users **/
    @GET("tweets")
    suspend fun getTweet(): Response<List<Tweet>>

    @GET("users/{id}/tweets")
    suspend fun getPostsByUser(@Path("id") userId: String): Response<List<Tweet>>


    /** In this API call we are getting the user credentials! **/
    @GET("users/{id}")
    suspend fun getLoggedInUserData(@Path("id") userId : String) : Response<User>

    /** In this API call we are update the user credentials! **/
    @PUT("users/{id}")
    fun editUser(
        @Body editProfile: EditProfile,
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): Call<EditProfile>

    @FormUrlEncoded
    @PATCH("users/{id}")
    fun updateUserUsername(@Path("id") userId: String, @Header("Authorization") token: String) : Call<EditProfile>

    @GET("users/{id}/likes")
    suspend fun getListOfLikedPosts(@Path("id")userId : String) : Response<List<Tweet>>

    @GET("tweets/{id}/replies")
    suspend fun getRepliesOfPost(@Path("id")tweetId : String) : Response<List<Tweet>>

    @GET("search")
    suspend fun searchNews(@Query("keyword") keyword: Tweet) : Response<Tweet>

    @POST("tweets/{id}/replies")
    fun postReply(
        @Body reply: ReplyContent,
        @Path("id")tweetId: String,
        @Header("Authorization") token: String
    ): Call<ReplyContent>

    @POST("users/{id}/profilePicture")
    fun loadImage(
        @Body image : ImageResource,
        @Path("id") userId : String,
        @Header("Authorization") token : String
    ) : Call<ImageResource>
}