package com.example.twitturin.profile.domain.repositoryImpl

import com.example.twitturin.profile.data.remote.api.ProfileApi
import com.example.twitturin.profile.data.remote.repository.ProfileRepository
import com.example.twitturin.profile.domain.model.EditProfile
import com.example.twitturin.profile.domain.model.ImageResource
import com.example.twitturin.profile.domain.model.User
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi
) : ProfileRepository {

    override fun editUser(
        editProfile: EditProfile,
        userId: String,
        token: String
    ): Call<EditProfile> {
        return profileApi.editUser(editProfile,userId, token)
    }

    override suspend fun getLoggedInUserData(userId: String): Response<User> {
        return profileApi.getLoggedInUserData(userId)
    }

    override suspend fun deleteUser(userId: String, token: String): Response<Unit> {
        return profileApi.deleteUser(userId, token)
    }

    override fun loadImage(
        picture: MultipartBody.Part,
        userId: String,
        token: String
    ):  Call<ImageResource> {
        return profileApi.loadImage(picture, userId, token)
    }
}