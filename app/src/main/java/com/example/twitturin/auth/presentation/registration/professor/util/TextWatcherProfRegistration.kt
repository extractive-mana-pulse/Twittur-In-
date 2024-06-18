package com.example.twitturin.auth.presentation.registration.professor.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import com.example.twitturin.R
import com.google.android.material.textfield.TextInputLayout

fun TextView.usernameRegistration(
    profUsernameInputLayout : TextInputLayout,
    signUpProf : Button,
    context: Context
) {

    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //
        }

        override fun afterTextChanged(s: Editable?) {

            val inputText = s?.toString()

            if (inputText != null && inputText.contains(" ")) {
                profUsernameInputLayout.error = context.resources.getString(R.string.no_spaces_allowed)
                signUpProf.isEnabled = false
            } else {
                profUsernameInputLayout.error = null
                signUpProf.isEnabled = true
            }
        }
    }
    this.addTextChangedListener(textWatcher)
}