package com.example.twitturin.feature.profile.domain

import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult
import com.example.twitturin.core.domain.util.Result

/** Contract for fetching + mutating a user profile. Implemented in :feature:profile:data over Ktor. */
interface ProfileRepository {
    suspend fun getUser(userId: String): Result<User, DataError.Network>
    suspend fun editProfile(userId: String, edit: EditProfile): EmptyResult<DataError.Network>
    suspend fun deleteAccount(userId: String): EmptyResult<DataError.Network>

    /** Upload a new avatar (`POST users/{id}/profilePicture`, multipart field `picture`). */
    suspend fun uploadProfilePicture(userId: String, imageBytes: ByteArray, fileName: String): EmptyResult<DataError.Network>

    /** Remove the avatar (`DELETE users/{id}/profilePicture`). */
    suspend fun deleteProfilePicture(userId: String): EmptyResult<DataError.Network>
}
