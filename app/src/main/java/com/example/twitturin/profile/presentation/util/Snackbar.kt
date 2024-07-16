package com.example.twitturin.profile.presentation.util

import android.view.View
import androidx.annotation.StringRes
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
//
//inline fun View.snack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
//    snack(resources.getString(messageRes), length, f)
//}
//
//inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
//    val snack = Snackbar.make(this, message, length)
//    snack.f()
//    snack.show()
//}
//
//fun Snackbar.action(@StringRes actionRes: Int, color: Int? = null, listener: (View) -> Unit) {
//    action(view.resources.getString(actionRes), color, listener)
//}
//
//fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
//    setAction(action, listener)
//    color?.let { setActionTextColor(ContextCompat.getColor(context, color)) }
//}