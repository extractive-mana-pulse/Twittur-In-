package com.example.twitturin.ui.fragments.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.twitturin.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LanguageFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_language, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = LanguageFragment()
    }
}