package com.example.twitturin.profile.domain.repository

import com.example.twitturin.profile.presentation.model.data.EditProfile
import com.example.twitturin.User
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

}