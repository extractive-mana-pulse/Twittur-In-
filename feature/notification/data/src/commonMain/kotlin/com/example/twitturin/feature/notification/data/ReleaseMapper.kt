package com.example.twitturin.feature.notification.data

import com.example.twitturin.feature.notification.domain.ReleaseInfo

fun ReleaseDto.toReleaseInfo(): ReleaseInfo = ReleaseInfo(
    name = name,
    tagName = tagName,
    body = body,
    publishedAt = publishedAt,
    htmlUrl = htmlUrl,
    downloadUrl = assets.firstOrNull()?.browserDownloadUrl,
)
