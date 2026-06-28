package com.example.twitturin.core.presentation

import com.example.twitturin.core.domain.util.DataError

/**
 * Maps shared data errors to user-facing text. Plain strings for now;
 * swap to UiText.FromResource (compose-resources) during the i18n/parity pass.
 */
fun DataError.toUiText(): UiText = UiText.DynamicString(
    when (this) {
        DataError.Network.NO_INTERNET -> "No internet connection."
        DataError.Network.REQUEST_TIMEOUT -> "Request timed out. Please try again."
        DataError.Network.UNAUTHORIZED -> "Your session has expired. Please sign in again."
        DataError.Network.FORBIDDEN -> "You don't have permission to do that."
        DataError.Network.NOT_FOUND -> "Not found."
        DataError.Network.CONFLICT -> "That conflicts with existing data."
        DataError.Network.TOO_MANY_REQUESTS -> "Too many requests. Please slow down."
        DataError.Network.PAYLOAD_TOO_LARGE -> "That upload is too large."
        DataError.Network.SERVER_ERROR -> "Something went wrong on our end."
        DataError.Network.SERVICE_UNAVAILABLE -> "Service temporarily unavailable."
        DataError.Network.SERIALIZATION -> "Couldn't read the server response."
        DataError.Network.BAD_REQUEST -> "Invalid request."
        DataError.Network.UNKNOWN -> "Something went wrong."
        DataError.Local.DISK_FULL -> "Your device storage is full."
        DataError.Local.NOT_FOUND -> "Not found."
        DataError.Local.UNKNOWN -> "Something went wrong."
    },
)
