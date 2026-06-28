package com.example.twitturin.feature.notification.data

import com.example.twitturin.core.data.network.get
import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.core.domain.util.map
import com.example.twitturin.feature.notification.domain.NotificationRepository
import com.example.twitturin.feature.notification.domain.ReleaseInfo
import io.ktor.client.HttpClient

class NotificationRepositoryImpl(
    private val httpClient: HttpClient,
) : NotificationRepository {

    override suspend fun getLatestRelease(): Result<ReleaseInfo, DataError.Network> {
        // Absolute GitHub URL — constructRoute() passes it through unchanged.
        return httpClient.get<ReleaseDto>(route = LATEST_RELEASE_URL)
            .map { dto -> dto.toReleaseInfo() }
    }

    private companion object {
        const val LATEST_RELEASE_URL =
            "https://api.github.com/repos/extractive-mana-pulse/Twittur-In-/releases/latest"
    }
}
