package com.example.twitturin.auth.presentation.registration.professor.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout

fun TextView.addAutoResizeTextWatcherOfProf(
    profUsernameInputLayout : TextInputLayout,
    signUpProf : Button
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
                profUsernameInputLayout.error = "No spaces allowed"
                signUpProf.isEnabled = false
            } else {
                profUsernameInputLayout.error = null
                signUpProf.isEnabled = true
            }
        }
    }
    this.addTextChangedListener(textWatcher)
}