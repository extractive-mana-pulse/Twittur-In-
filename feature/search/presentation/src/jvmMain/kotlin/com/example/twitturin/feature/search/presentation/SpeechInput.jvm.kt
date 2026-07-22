package com.example.twitturin.feature.search.presentation

import androidx.compose.runtime.Composable

/** Desktop has no system speech recognizer; speech input is not offered (mic hidden). */
@Composable
actual fun rememberSpeechInput(onResult: (String) -> Unit): (() -> Unit)? = null
