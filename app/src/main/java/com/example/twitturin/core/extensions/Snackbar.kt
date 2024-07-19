package com.example.twitturin.core.extensions

import android.view.View
import androidx.core.content.ContextCompat
import com.example.twitturin.R
import com.google.android.material.snackbar.Snackbar

inline fun View.snackbarError(
    anchorView: View,
    error: String,
    actionText: String,
    crossinline actionCallback: () -> Unit
) {
    val duration = Snackbar.LENGTH_SHORT

    val snackbar = Snackbar
        .make(this, error, duration)
        .setBackgroundTint(ContextCompat.getColor(context, R.color.md_theme_light_errorContainer))
        .setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onErrorContainer))
        .setActionTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onErrorContainer))
        .setAnchorView(anchorView)
        .setAction(actionText) {
            actionCallback.invoke()
        }
    snackbar.show()
}

fun View.snackbar(
    anchorView: View,
    message : String
) {
    val duration = Snackbar.LENGTH_SHORT

    val snackbar = Snackbar
        .make(this, message, duration)
        .setBackgroundTint(resources.getColor(R.color.md_theme_light_inverseSurface))
        .setTextColor(resources.getColor(R.color.md_theme_light_inverseOnSurface))
        .setAnchorView(anchorView)
    snackbar.show()
}