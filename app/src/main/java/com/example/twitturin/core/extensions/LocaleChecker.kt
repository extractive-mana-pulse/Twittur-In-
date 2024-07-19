package com.example.twitturin.core.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
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