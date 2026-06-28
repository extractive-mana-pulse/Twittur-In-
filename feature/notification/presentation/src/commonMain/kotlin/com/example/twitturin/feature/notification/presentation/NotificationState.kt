package com.example.twitturin.feature.notification.presentation

import com.example.twitturin.feature.notification.domain.ReleaseInfo

data class NotificationState(
    val isLoading: Boolean = false,
    val release: ReleaseUi? = null,
)

/** Display-ready model for the latest release (no nullable display fields). */
data class ReleaseUi(
    val title: String,
    val tagName: String,
    val body: String,
    val publishedAt: String,
    val downloadUrl: String?,
)

fun ReleaseInfo.toReleaseUi(): ReleaseUi = ReleaseUi(
    title = name ?: tagName ?: "Latest release",
    tagName = tagName.orEmpty(),
    body = body.orEmpty(),
    publishedAt = publishedAt.orEmpty(),
    downloadUrl = downloadUrl,
)
