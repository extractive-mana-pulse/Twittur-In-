package com.example.twitturin.core.extensions

import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment

fun Fragment.retry(view:View, editText: EditText,editText1: EditText) {
    view.showKeyboard()
    editText.text?.clear()
    editText1.text?.clear()
}