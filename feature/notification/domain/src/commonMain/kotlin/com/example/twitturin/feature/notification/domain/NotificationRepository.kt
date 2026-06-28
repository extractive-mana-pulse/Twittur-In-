package com.example.twitturin.feature.notification.domain

import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.Result

/** Contract for fetching app release info. Implemented in :feature:notification:data over Ktor. */
interface NotificationRepository {
    suspend fun getLatestRelease(): Result<ReleaseInfo, DataError.Network>
}
