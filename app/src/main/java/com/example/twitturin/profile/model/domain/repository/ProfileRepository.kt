package com.example.twitturin.profile.model.domain.repository

import com.example.twitturin.model.data.editUser.EditProfile
import com.example.twitturin.model.data.users.User
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