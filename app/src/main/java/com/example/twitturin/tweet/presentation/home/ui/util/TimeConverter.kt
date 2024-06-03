package com.example.twitturin.tweet.presentation.home.ui.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun String.formatCreatedAtPost(): String {
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
            weeks > 0 -> "$weeks w."
            days > 0 -> "$days d."
            hours > 0 -> "$hours h."
            minutes > 0 -> "$minutes min."
            else -> "$seconds sec."
        }
    } catch (e: Exception) {
        "Invalid date"
    }
}