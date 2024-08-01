package com.example.twitturin.core.extensions

import androidx.appcompat.app.AppCompatActivity
import com.example.twitturin.R
import com.example.twitturin.core.preferences.MyPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationBarView

fun AppCompatActivity.checkStatus(bottomNavigationView: BottomNavigationView) {

    when (MyPreferences(this).labelStatus) {
        0 -> {
            bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        }
        1 -> {
            bottomNavigationView.labelVisibilityMode =NavigationBarView.LABEL_VISIBILITY_UNLABELED
        }
        2 -> {
            bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED
        }
    }
}

fun android.app.Activity.appBNVDialog(bottomNavigationView: BottomNavigationView) {
    val builder = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
    builder.setTitle(resources.getString(R.string.change_theme))
    val styles = arrayOf("Label", "Un label", "Selected")
    val checkedItem = MyPreferences(this).labelStatus

    builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
        when (which) {
            0 -> {
                bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
                MyPreferences(this).labelStatus = 0
                dialog.dismiss()
            }
            1 -> {
                bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_UNLABELED
                MyPreferences(this).labelStatus = 1
                dialog.dismiss()
            }
            2 -> {
                bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED
                MyPreferences(this).labelStatus = 2
                dialog.dismiss()
            }
        }
    }
    val dialog = builder.create()
    dialog.show()
}