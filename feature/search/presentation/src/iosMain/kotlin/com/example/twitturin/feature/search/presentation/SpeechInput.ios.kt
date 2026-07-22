package com.example.twitturin.feature.search.presentation

import androidx.compose.runtime.Composable

/** iOS has no lightweight system recognizer intent; speech input is not offered (mic hidden). */
@Composable
actual fun rememberSpeechInput(onResult: (String) -> Unit): (() -> Unit)? = null
