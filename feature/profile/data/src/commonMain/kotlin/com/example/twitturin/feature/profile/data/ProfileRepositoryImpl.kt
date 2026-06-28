package com.example.twitturin.feature.profile.data

import com.example.twitturin.core.data.network.delete
import com.example.twitturin.core.data.network.get
import com.example.twitturin.core.data.network.put
import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult
import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.core.domain.util.map
import com.example.twitturin.feature.profile.domain.EditProfile
import com.example.twitturin.feature.profile.domain.ProfileRepository
import com.example.twitturin.feature.profile.domain.User
import io.ktor.client.HttpClient

class ProfileRepositoryImpl(
    private val httpClient: HttpClient,
) : ProfileRepository {

    override suspend fun getUser(userId: String): Result<User, DataError.Network> {
        return httpClient.get<UserDto>(route = "users/$userId").map { it.toUser() }
    }

    override suspend fun editProfile(userId: String, edit: EditProfile): EmptyResult<DataError.Network> {
        // Response type is Unit: the server returns the updated user object, whose shape
        // doesn't match the request DTO (no `password`/etc.). We only need success/failure,
        // so discard the body to avoid a serialization failure on a successful 2xx.
        return httpClient.put<EditProfileDto, Unit>(
            route = "users/$userId",
            body = edit.toDto(),
        )
    }

    override suspend fun deleteAccount(userId: String): EmptyResult<DataError.Network> {
        return httpClient.delete<Unit>(route = "users/$userId")
    }
}
