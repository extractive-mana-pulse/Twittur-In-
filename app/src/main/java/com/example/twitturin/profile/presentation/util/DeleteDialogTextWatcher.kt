package com.example.twitturin.profile.presentation.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

fun TextView.deleteDialogEmailWatcher(emailConfirmBtn : ImageButton, emailEt : EditText) {

    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            emailConfirmBtn.isEnabled = !emailEt.text.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {
            //
        }
    }
    this.addTextChangedListener(textWatcher)
}

fun TextView.deleteDialogCodeWatcher(deleteBtn : LinearLayout, codeEt : EditText) {

    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            deleteBtn.isEnabled = !codeEt.text.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {
            //
        }
    }
    this.addTextChangedListener(textWatcher)
}