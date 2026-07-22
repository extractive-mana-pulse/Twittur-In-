package com.example.twitturin.feature.notification.presentation

import androidx.compose.runtime.Composable

/**
 * Invisible effect that asks for the platform's notification permission when the notifications
 * screen is opened and the permission hasn't been granted yet.
 *
 * Android 13+: launches the `POST_NOTIFICATIONS` runtime permission dialog.
 * iOS: requests authorization from `UNUserNotificationCenter`.
 * Desktop: no-op (no notification permission concept).
 */
@Composable
expect fun RequestNotificationPermissionEffect()
