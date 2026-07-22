package com.example.twitturin.feature.timetable.presentation

import androidx.compose.runtime.Composable

/**
 * Platform document picker for importing the timetable XML export. Forked from
 * feature/profile's `ImagePicker` (same shape, filtered to documents instead of images).
 *
 * Returns a launcher that opens the platform's file picker, and delivers the chosen file's bytes
 * + file name through [onPicked], or `null` on platforms without a picker (the upload affordance
 * is hidden there).
 */
@Composable
expect fun rememberTimetableDocumentPicker(onPicked: (bytes: ByteArray, fileName: String) -> Unit): (() -> Unit)?
