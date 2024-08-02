package com.example.twitturin.core.extensions

import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
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

        setPositiveButton(resources.getString(R.string.yes)) { _, _ -> actionYesClicked.invoke() }

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
    val emailEt = dialogView.findViewById<EditText>(R.id.email_confirm_et)
    val codeEt = dialogView.findViewById<EditText>(R.id.code_sent_from_email_et)
    val emailConfirmBtn = dialogView.findViewById<ImageButton>(R.id.email_confirm_btn)

    emailConfirmBtn.beGone()

    codeEt.deleteDialogCodeWatcher(deleteBtn, codeEt)

    emailEt.deleteDialogEmailWatcher(emailConfirmBtn, emailEt)

    emailConfirmBtn.setOnClickListener {
        // todo: logic of code when email is pressed
        actionEmailPressed.invoke()
    }

    cancelBtn.setOnClickListener {
        // todo: logic of code to cancel all actions of custom Dialog.
        actionCancelPressed.invoke().apply { alertDialog.dismiss() }
    }

    deleteBtn.stateDisabled()
    deleteBtn.setOnClickListener {
        // todo: write code. when delete clicked
        actionDeletePressed.invoke()
        alertDialog.dismiss()
    }
    alertDialog.show()
}