package com.example.twitturin.core.helper

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.core.extensions.beVisible

fun Context.getAppVersion(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName // Get version name as a string
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}

fun compareVersions(version1: String, version2: String): Int {
    val version1Parts = version1.split(".").map { it.toInt() }
    val version2Parts = version2.split(".").map { it.toInt() }

    val maxLength = maxOf(version1Parts.size, version2Parts.size)
    for (i in 0 until maxLength) {
        val v1 = if (i < version1Parts.size) version1Parts[i] else 0
        val v2 = if (i < version2Parts.size) version2Parts[i] else 0
        if (v1 != v2) return v1 - v2
    }
    return 0 // Versions are equal
}

fun Context.checkForUpdates(
    latestReleaseTag: String, // Keep as String for version comparison
    anView: View,
    emptyTvLayout: View,
    downloadLayout: View
) {
    val currentVersion = this.getAppVersion()

    if (currentVersion != "Unknown") {
        when {
            compareVersions(currentVersion, latestReleaseTag) < 0 -> {
                anView.beGone()
                emptyTvLayout.beGone()
                downloadLayout.beVisible()
            }
            compareVersions(currentVersion, latestReleaseTag) == 0 -> {
                anView.beVisible()
                emptyTvLayout.beVisible()
                downloadLayout.beGone()
            }
            else -> {
                // Current version is greater than latestReleaseTag
                anView.beVisible()
                emptyTvLayout.beVisible()
                downloadLayout.beGone()
            }
        }
    } else {
        // Handle error case, e.g., log or show a message
    }
}