package com.example.twitturin.core.extensions

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import androidx.fragment.app.Fragment

fun Fragment.retry(view:View, editText: EditText,editText1: EditText) {
    view.showKeyboard()
    editText.text?.clear()
    editText1.text?.clear()
}

// use this for follow & unfollow btn.
fun View.beVisibleIf(isVisible: Boolean) = if (isVisible) beVisible() else beGone()

fun View.beVisible() {
    visibility = VISIBLE
}

fun View.beGone() {
    visibility = GONE
}

fun View.stateDisabled(){
    isEnabled = false
}

fun View.stateEnabled(){
    isEnabled = true
}

//val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
//
//windowInsetsController.hide(Type.systemBars())
//
//windowInsetsController.show(Type.systemBars())