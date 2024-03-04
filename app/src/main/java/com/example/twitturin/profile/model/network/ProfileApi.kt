package com.example.twitturin.profile.model.network

import com.example.twitturin.profile.model.data.EditProfile
import com.example.twitturin.auth.model.data.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
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

}