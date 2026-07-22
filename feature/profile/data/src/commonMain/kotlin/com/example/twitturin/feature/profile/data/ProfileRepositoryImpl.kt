package com.example.twitturin.feature.profile.data

import com.example.twitturin.core.data.network.constructRoute
import com.example.twitturin.core.data.network.delete
import com.example.twitturin.core.data.network.get
import com.example.twitturin.core.data.network.put
import com.example.twitturin.core.data.network.safeCall
import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult
import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.core.domain.util.map
import com.example.twitturin.feature.profile.domain.EditProfile
import com.example.twitturin.feature.profile.domain.ProfileRepository
import com.example.twitturin.feature.profile.domain.User
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

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

    override suspend fun uploadProfilePicture(
        userId: String,
        imageBytes: ByteArray,
        fileName: String,
    ): EmptyResult<DataError.Network> {
        // Multipart per the API spec (`UpdateProfilePicture`: binary field `picture`); the
        // multipart body's own content type overrides the client-default JSON one.
        return safeCall<Unit> {
            httpClient.submitFormWithBinaryData(
                url = constructRoute("users/$userId/profilePicture"),
                formData = formData {
                    append(
                        key = "picture",
                        value = imageBytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, imageContentTypeFor(fileName))
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        },
                    )
                },
            )
        }
    }

    override suspend fun deleteProfilePicture(userId: String): EmptyResult<DataError.Network> {
        return httpClient.delete<Unit>(route = "users/$userId/profilePicture")
    }

    private fun imageContentTypeFor(fileName: String): String = when (fileName.substringAfterLast('.', "").lowercase()) {
        "png" -> "image/png"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        "heic" -> "image/heic"
        else -> "image/jpeg"
    }
}
