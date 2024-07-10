package com.example.twitturin.profile.data.remote.repository

import com.example.twitturin.profile.domain.model.EditProfile
import com.example.twitturin.profile.domain.model.ImageResource
import com.example.twitturin.profile.domain.model.User
import retrofit2.Call
import retrofit2.Response

interface ProfileRepository {

    fun editUser(
        editProfile: EditProfile,
        userId: String,
        token: String
    ): Call<EditProfile>

    suspend fun getLoggedInUserData(userId : String) : Response<User>

    suspend fun deleteUser(userId: String, token: String): Response<Unit>

    fun loadImage(
        image : ImageResource,
        userId : String,
        token : String
    ) : Result<String>

}