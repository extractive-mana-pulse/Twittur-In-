package com.example.twitturin.core.extensions

import android.annotation.SuppressLint
import android.util.Log
import android.widget.PopupMenu

@SuppressLint("DiscouragedPrivateApi")
fun PopupMenu.converter(popupMenu: PopupMenu){

    try {
        val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
        fieldMPopup.isAccessible = true
        val mPopup = fieldMPopup.get(popupMenu)
        mPopup.javaClass
            .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
            .invoke(mPopup, true)
    } catch (e: Exception){
        Log.e("Main", "Error showing menu icons.", e)
    } finally {
        popupMenu.show()
    }

}