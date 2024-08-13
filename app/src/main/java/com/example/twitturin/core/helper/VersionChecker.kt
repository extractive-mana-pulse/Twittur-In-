package com.example.twitturin.core.helper

import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.core.extensions.beVisible

fun Context.getAppVersion(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionCode.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}

fun Context.checkForUpdates(
    latestReleaseTag: String,
    anView: View,
    emptyTvLayout: View,
    downloadLayout: View
) {
    val currentVersion = this.getAppVersion()
    if (currentVersion != "Unknown") {
        if (currentVersion < latestReleaseTag) {
            anView.beGone()
            emptyTvLayout.beGone()
            downloadLayout.beVisible()
        } else if (currentVersion == latestReleaseTag) {
            anView.beVisible()
            emptyTvLayout.beVisible()
            downloadLayout.beGone()
        }
    }
}