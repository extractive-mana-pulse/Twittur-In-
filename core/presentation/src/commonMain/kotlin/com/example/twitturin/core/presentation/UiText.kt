package com.example.twitturin.core.presentation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * A string that is either dynamic or comes from a (multiplatform) string resource.
 * Use for any text that could be localized — especially error messages.
 */
sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    data class FromResource(
        val id: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText

    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value
        is FromResource -> stringResource(id, *args.toTypedArray())
    }
}
