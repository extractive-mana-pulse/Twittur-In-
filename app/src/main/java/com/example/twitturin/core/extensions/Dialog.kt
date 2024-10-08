package com.example.twitturin.core.extensions

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.twitturin.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder


inline fun Fragment.defaultDialog(
    title: String,
    message: String,
    crossinline actionYesClicked: () -> Unit,
    crossinline actionNoClicked: () -> Unit
) {
    val alertDialogBuilder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
    alertDialogBuilder.apply {
        setTitle(title)
        setMessage(message)

        setPositiveButton(resources.getString(R.string.yes)) { dialog, _ -> actionYesClicked.invoke().apply { dialog.dismiss() } }

        setNegativeButton(resources.getString(R.string.no)) { dialog, _ -> actionNoClicked.invoke().apply { dialog.dismiss() } }

        setCancelable(true)
        val alertDialog = create()
        alertDialog.show()
    }
}

inline fun Fragment.customDialog(
    crossinline actionEmailPressed: () -> Unit,
    crossinline actionCancelPressed: () -> Unit,
    crossinline actionDeletePressed: () -> Unit,
){
    val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
    val inflater = layoutInflater
    val dialogView = inflater.inflate(R.layout.custom_dialog, null)
    builder.setView(dialogView)
    val alertDialog = builder.create()

    val cancelBtn = dialogView.findViewById<LinearLayout>(R.id.cancel_btn)
    val deleteBtn = dialogView.findViewById<LinearLayout>(R.id.delete_btn)
//    val emailEt = dialogView.findViewById<EditText>(R.id.email_confirm_et)
//    val codeEt = dialogView.findViewById<EditText>(R.id.code_sent_from_email_et)
//    val emailConfirmBtn = dialogView.findViewById<ImageButton>(R.id.email_confirm_btn)

//    emailConfirmBtn.beGone()

//    codeEt.deleteDialogCodeWatcher(deleteBtn, codeEt)

//    emailEt.deleteDialogEmailWatcher(emailConfirmBtn, emailEt)

//    emailConfirmBtn.setOnClickListener {
        // todo: logic of code when email is pressed
//        actionEmailPressed.invoke()
//    }

    cancelBtn.setOnClickListener {
        // todo: logic of code to cancel all actions of custom Dialog.
        actionCancelPressed.invoke().apply { alertDialog.dismiss() }
    }

    deleteBtn.stateEnabled()
    deleteBtn.setOnClickListener {
        // todo: write code. when delete clicked
        actionDeletePressed.invoke().apply { alertDialog.dismiss() }
    }
    alertDialog.show()
}

fun Activity.dialogSuccess(){
    val builder = AlertDialog.Builder(this,R.style.ThemeOverlay_App_MaterialAlertDialog).create()
    val view = layoutInflater.inflate(R.layout.custom_success,null)
    builder.setView(view)
    builder.setCanceledOnTouchOutside(false)
    builder.show()

    Handler(Looper.getMainLooper()).postDelayed({
        builder.dismiss()
    }, 3000)
}