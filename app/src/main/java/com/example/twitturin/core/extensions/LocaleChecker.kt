package com.example.twitturin.core.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.example.twitturin.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

fun AppCompatActivity.checkLocale(language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = Configuration()
    config.setLocale(locale)
    baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

    val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
    editor.putString("language", language)
    editor.apply()
}

fun AppCompatActivity.loadLocale() {
    val sharedPref = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
    val language = sharedPref.getString("language","")
    language?.let { this.checkLocale(it) }
}

fun Activity.setLocale(lang : String) {
    val locale = Locale(lang)
    Locale.setDefault(locale)
    val config = Configuration()
    config.setLocale(locale)
    baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

    val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
    editor.putString("lang", lang)
    editor.apply()
}

fun Activity.appLanguage() {
    val builder = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
    builder.setTitle(resources.getString(R.string.choose_language))
    val styles = arrayOf("en", "it", "ru", "uz")
    builder.setSingleChoiceItems(styles, -1) { dialog, which ->
        when (which) {
            0 -> {
                setLocale("en")
                recreate()
            }
            1 -> {
                setLocale("it")
                recreate()
            }
            2 -> {
                setLocale("ru")
                recreate()
            }
            3 -> {
                setLocale("uz")
                recreate()
            }
        }
        dialog.dismiss()
    }
    val dialog = builder.create()
    dialog.show()
}