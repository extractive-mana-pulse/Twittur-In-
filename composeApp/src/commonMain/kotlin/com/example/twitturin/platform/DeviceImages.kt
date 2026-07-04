package com.example.twitturin.platform

import androidx.compose.runtime.Composable

/**
 * Returns up to [limit] recent gallery image URIs from the device, when read permission has been
 * granted. Android queries MediaStore; iOS/Desktop return an empty list (gallery access there is
 * out of scope — publishing pictures isn't enabled yet either).
 */
@Composable
expect fun rememberRecentDeviceImages(limit: Int): List<String>
