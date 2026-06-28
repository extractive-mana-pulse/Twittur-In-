package com.example.twitturin.feature.notification.domain

/**
 * Domain model for the latest GitHub release shown on the notifications / patch-notes screens.
 * Trimmed from the GitHub releases payload to the fields the UI actually renders.
 */
data class ReleaseInfo(
    val name: String?,
    val tagName: String?,
    val body: String?,
    val publishedAt: String?,
    val htmlUrl: String?,
    val downloadUrl: String?,
)
