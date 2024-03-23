package com.example.twitturin.profile.domain.repositoryImpl

import com.example.twitturin.profile.presentation.model.data.EditProfile
import com.example.twitturin.auth.presentation.model.data.User
import com.example.twitturin.profile.domain.repository.ProfileRepository
import com.example.twitturin.profile.presentation.model.network.ProfileApi
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
}