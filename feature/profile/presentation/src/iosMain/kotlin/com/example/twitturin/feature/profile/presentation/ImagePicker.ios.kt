package com.example.twitturin.feature.profile.presentation

import androidx.compose.runtime.Composable

/** iOS photo picking (PHPicker) is not wired yet — the change-photo affordance is hidden. */
@Composable
actual fun rememberImagePicker(onPicked: (bytes: ByteArray, fileName: String) -> Unit): (() -> Unit)? = null
