package com.example.twitturin.core.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.twitturin.R
import com.google.android.material.textfield.TextInputLayout

fun TextView.usernameRegistration(
    profUsernameInputLayout : TextInputLayout,
    signUpProf : Button,
    context: Context
) {

    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }

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

fun List<EditText>.listOfEditTexts(signUpProf: Button) {
    forEach { editText ->
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val allFieldsFilled = all { it.text.isNotBlank() }
                signUpProf.isVisible = allFieldsFilled
            }
        })
    }
}

fun TextView.login(
    usernameSignInEt : EditText,
    passwordEt : EditText,
    signIn : Button
) {

    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            signIn.isEnabled = !usernameSignInEt.text.isNullOrBlank() && !passwordEt.text.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {  }
    }
    this.addTextChangedListener(textWatcher)
}

fun TextView.addAutoResizeTextWatcher(sentReply : ImageButton) {

    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            sentReply.isEnabled = !s.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {
            //
        }
    }
    this.addTextChangedListener(textWatcher)
}

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

fun TextView.addTextButtonEnables(tweetBtn : Button, contentEt : EditText) {

    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            tweetBtn.isEnabled = !contentEt.text.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {
            //
        }
    }
    this.addTextChangedListener(textWatcher)
}