package com.example.twitturin.core.extensions

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.sendEmail(subject: String, message: String) {
    /**ACTION_SENDTO action to launch an email client installed on your Android device.*/
    val mIntent = Intent(Intent.ACTION_SENDTO)
    /**To send an email you need to specify mailto: as URI using setData() method\
    and data type will be to text/plain using setType() method*/
    mIntent.data = Uri.parse("mailto:")
    mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("invoker1441@gmail.com"))
    mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    mIntent.putExtra(Intent.EXTRA_TEXT, message)

    try {
        startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
    }
    catch (e: Exception){
        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
    }
}