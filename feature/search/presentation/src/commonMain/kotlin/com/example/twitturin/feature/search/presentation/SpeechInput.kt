package com.example.twitturin.feature.search.presentation

import androidx.compose.runtime.Composable

/**
 * Platform speech-to-text behind the search field's mic.
 *
 * Returns a launcher that opens the platform's speech recognizer and delivers the recognized
 * phrase through [onResult], or `null` on platforms without speech input (the mic is hidden).
 */
@Composable
expect fun rememberSpeechInput(onResult: (String) -> Unit): (() -> Unit)?
