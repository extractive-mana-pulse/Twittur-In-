package com.example.twitturin.feature.notification.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseDto(
    @SerialName("name") val name: String? = null,
    @SerialName("tag_name") val tagName: String? = null,
    @SerialName("body") val body: String? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("html_url") val htmlUrl: String? = null,
    @SerialName("assets") val assets: List<ReleaseAssetDto> = emptyList(),
)

@Serializable
data class ReleaseAssetDto(
    @SerialName("name") val name: String? = null,
    @SerialName("browser_download_url") val browserDownloadUrl: String? = null,
)
