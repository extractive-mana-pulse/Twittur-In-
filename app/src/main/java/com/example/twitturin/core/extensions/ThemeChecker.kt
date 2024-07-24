package com.example.twitturin.core.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.twitturin.R
import com.example.twitturin.home.presentation.preferences.MyPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun AppCompatActivity.checkTheme() {
    when (MyPreferences(this).darkMode) {
        0 -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        1 -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        2 -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}

fun android.app.Activity.appThemeDialog() {
    val builder = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
    builder.setTitle(resources.getString(R.string.change_theme))
    val styles = arrayOf("Light", "Dark", "System default")
    val checkedItem = MyPreferences(this).darkMode

    builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
        when (which) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                MyPreferences(this).darkMode = 0
                dialog.dismiss()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                MyPreferences(this).darkMode = 1
                dialog.dismiss()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                MyPreferences(this).darkMode = 2
                dialog.dismiss()
            }
        }
    }
    val dialog = builder.create()
    dialog.show()
}