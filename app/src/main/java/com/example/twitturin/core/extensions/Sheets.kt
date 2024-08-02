package com.example.twitturin.core.extensions

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

fun BottomSheetDialogFragment.expandedSheet(){

    val bottomSheet : FrameLayout = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
    bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    val behavior = BottomSheetBehavior.from(bottomSheet)

    behavior.apply {
        isDraggable = false
        peekHeight = resources.displayMetrics.heightPixels
        state = BottomSheetBehavior.STATE_EXPANDED

        addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }
}