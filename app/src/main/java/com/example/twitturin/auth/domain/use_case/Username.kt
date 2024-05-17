package com.example.twitturin.auth.domain.use_case

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.example.twitturin.R
import com.example.twitturin.auth.domain.model.ValidationResult

class Username {

//    fun execute(email: String): ValidationResult {
//        if(email.isBlank()) {
//            return ValidationResult(
//                successful = false,
//                errorMessage = "The email can't be blank"
//            )
//        }
//        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            return ValidationResult(
//                successful = false,
//                errorMessage = "That's not a valid email"
//            )
//        }
//        return ValidationResult(
//            successful = true
//        )
//    }

    fun execute(username: String, context: Context): ValidationResult {
        if (username.isBlank() && username.contains(" ")) {
            return ValidationResult(
                successful = false,
                errorMessage = context.resources.getString(R.string.no_spaces_allowed)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}
//        username.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                //
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                //
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                val inputText = s?.toString()
//
//                if (inputText != null && inputText.contains(" ")) {
//                    studentUsernameInputLayout.error = resources.getString(R.string.no_spaces_allowed)
//                    studentSignUpBtn.isEnabled = false
//                } else {
//                    studentUsernameInputLayout.error = null
//                    studentSignUpBtn.isEnabled = true
//                }
//            }
//        })