package com.example.twitturin.profile.data.remote.repository

import com.example.twitturin.profile.data.remote.api.photoUrl
import com.example.twitturin.profile.domain.model.EditProfile
import com.example.twitturin.profile.domain.model.User
import okhttp3.MultipartBody
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
        userId : String,
        picture : MultipartBody.Part,
        token : String
    ) : Call<photoUrl> /*Result<String>*/
}