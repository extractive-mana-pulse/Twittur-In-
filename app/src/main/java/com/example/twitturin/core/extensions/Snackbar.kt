package com.example.twitturin.core.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.example.twitturin.R
import com.example.twitturin.databinding.CustomSnackbarBinding
import com.google.android.material.snackbar.Snackbar

inline fun View.snackbarError(
    anchorView: View,
    error: String,
    actionText: String,
    crossinline actionCallback: () -> Unit
) {
    val duration = Snackbar.ANIMATION_MODE_SLIDE

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
    val duration = Snackbar.ANIMATION_MODE_SLIDE

    val snackbar = Snackbar
        .make(this, message, duration)
        .setBackgroundTint(resources.getColor(R.color.md_theme_light_inverseSurface))
        .setTextColor(resources.getColor(R.color.md_theme_light_inverseOnSurface))
        .setAnchorView(anchorView)
        .setTextMaxLines(5)
        .setDuration(5000)

    snackbar.show()
}

@SuppressLint("RestrictedApi")
fun View.showCustomSnackbar(
    anchorView: View,
    iconResId: Int? = null,
    context: Context,
    message: String
) {
    val snackbarView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar, null)
    val bind = CustomSnackbarBinding.bind(snackbarView)

    val duration = Snackbar.ANIMATION_MODE_SLIDE

    bind.apply {
        snackbarContext.text = message
        iconResId?.let { snackbarIcon.setImageResource(it) }
    }
    val snackbar = Snackbar
        .make(this, "", duration)
        .setAnchorView(anchorView)
        .setTextMaxLines(2)
        .setDuration(2500)

    val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
    snackbarLayout.setPadding(0, 0, 0, 0)
    snackbarLayout.addView(snackbarView, 0)
    snackbar.show()
}