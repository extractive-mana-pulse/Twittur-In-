package com.example.twitturin.platform

import androidx.compose.runtime.Composable

/**
 * Returns a platform share function. Android opens the system share sheet (ACTION_SEND),
 * iOS presents a `UIActivityViewController`, Desktop copies the text to the clipboard.
 */
@Composable
expect fun rememberShareHandler(): (String) -> Unit
