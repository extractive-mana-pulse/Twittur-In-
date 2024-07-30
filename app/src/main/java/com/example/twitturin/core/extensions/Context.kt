package com.example.twitturin.core.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun Context.shareUrl(text: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, null))
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

fun Context.copyToClipboard(text: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Copied Text", text)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

fun Context.toastCustom(text: String) { Toast.makeText(this, text, Toast.LENGTH_SHORT).show() }