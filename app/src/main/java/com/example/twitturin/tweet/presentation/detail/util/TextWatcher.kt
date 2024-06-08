package com.example.twitturin.tweet.presentation.detail.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageButton
import android.widget.TextView

fun TextView.addAutoResizeTextWatcher(sentReply : ImageButton) {

    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            sentReply.isEnabled = !s.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {
//            this@addAutoResizeTextWatcher.post {
//                val lineCount = this@addAutoResizeTextWatcher.lineCount
//                val lineHeight = this@addAutoResizeTextWatcher.lineHeight
//                val desiredHeight = lineCount * lineHeight
//
//                val layoutParams = this@addAutoResizeTextWatcher.layoutParams
//                layoutParams.height = desiredHeight
//                this@addAutoResizeTextWatcher.layoutParams = layoutParams
//            }
        }
    }
    this.addTextChangedListener(textWatcher)
}