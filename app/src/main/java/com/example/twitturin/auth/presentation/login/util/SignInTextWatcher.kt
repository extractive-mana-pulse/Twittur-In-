package com.example.twitturin.auth.presentation.login.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

fun TextView.login(
    usernameSignInEt : EditText,
    passwordEt : EditText,
    signIn : Button
) {

    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            signIn.isEnabled = !usernameSignInEt.text.isNullOrBlank() && !passwordEt.text.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {
            //
        }
    }
    this.addTextChangedListener(textWatcher)
}