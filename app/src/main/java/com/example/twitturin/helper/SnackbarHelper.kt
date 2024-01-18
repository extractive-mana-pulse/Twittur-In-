package com.example.twitturin.helper

import android.content.res.Resources
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.example.twitturin.R
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class SnackbarHelper @Inject constructor(private val resources: Resources) {

    fun snackbarError(view: View, anchorView : View, error: String, actionText : String, actionCallback: () -> Unit) {
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = Snackbar
            .make(view, error, duration)
            .setBackgroundTint(resources.getColor(R.color.md_theme_light_errorContainer))
            .setTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
            .setActionTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
            .setAnchorView(anchorView)
            .setAction(actionText) {
                actionCallback.invoke()
            }
        snackbar.show()
    }

    fun snackbar(view: View, anchorView: View, message : String) {
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = Snackbar
            .make(view, message, duration)
            .setBackgroundTint(resources.getColor(R.color.md_theme_light_inverseSurface))
            .setTextColor(resources.getColor(R.color.md_theme_light_inverseOnSurface))
            .setAnchorView(anchorView)
        snackbar.show()
    }
}