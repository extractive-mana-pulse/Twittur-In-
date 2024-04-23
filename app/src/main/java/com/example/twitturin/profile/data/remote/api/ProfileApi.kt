package com.example.twitturin.profile.data.remote.api

import com.example.twitturin.profile.domain.model.EditProfile
import com.example.twitturin.profile.domain.model.User
import com.example.twitturin.profile.domain.model.ImageResource
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileApi {

    /** edit user credentials */
    @PUT("users/{id}")
    fun editUser(
        @Body editProfile: EditProfile,
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): Call<EditProfile>

    /** get user credentials **/
    @GET("users/{id}")
    suspend fun getLoggedInUserData(@Path("id") userId : String) : Response<User>

    /** delete account **/
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: String, @Header("Authorization") token: String): Response<Unit>

    @POST("users/{id}/profilePicture")
    fun loadImage(
        @Body image : ImageResource,
        @Path("id") userId : String,
        @Header("Authorization") token : String
    ) : Call<ImageResource>
}