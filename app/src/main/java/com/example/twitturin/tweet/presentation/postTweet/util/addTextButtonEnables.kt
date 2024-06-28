package com.example.twitturin.tweet.presentation.postTweet.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

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