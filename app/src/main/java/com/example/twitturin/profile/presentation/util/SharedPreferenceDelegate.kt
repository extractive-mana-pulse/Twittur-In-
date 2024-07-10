package com.example.twitturin.profile.presentation.util

import android.content.Context
import androidx.activity.ComponentActivity
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreferenceDelegate(
    private val context : Context,
    private val name : String,
    private val defaultValue : String = ""
): ReadWriteProperty<Any?, String> {

    private val sharedPreferences by lazy { context.getSharedPreferences("my_prefs_edit_page", Context.MODE_PRIVATE) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return sharedPreferences.getString(name, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        sharedPreferences.edit().putString(name, value).apply()
    }
}

fun Context.sharedPreferences(name: String) = SharedPreferenceDelegate(this, name)