package com.example.twitturin.feature.profile.presentation

import androidx.compose.runtime.Composable

/**
 * Platform image picker for the profile-picture upload.
 *
 * Returns a launcher that opens the platform's photo picker and delivers the chosen image's
 * bytes + file name through [onPicked], or `null` on platforms without a picker (the change-photo
 * affordance is hidden there).
 */
@Composable
expect fun rememberImagePicker(onPicked: (bytes: ByteArray, fileName: String) -> Unit): (() -> Unit)?
