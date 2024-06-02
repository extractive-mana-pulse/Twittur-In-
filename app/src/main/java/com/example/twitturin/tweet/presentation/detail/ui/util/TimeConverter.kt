package com.example.twitturin.tweet.presentation.detail.ui.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun String.formatCreatedAt(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    return try {
        val date = dateFormat.parse(this)
        val currentTime = System.currentTimeMillis()

        val durationMillis = currentTime - date!!.time

        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
        val days = TimeUnit.MILLISECONDS.toDays(durationMillis)
        val weeks = days / 7

        when {
            weeks > 0 -> "$weeks weeks ago"
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "$seconds seconds ago"
        }
    } catch (e: Exception) {
        "Invalid date"
    }
}