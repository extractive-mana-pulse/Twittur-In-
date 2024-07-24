package com.example.twitturin.home.presentation.ui

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentLanguageBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale

class LanguageBottomSheetFragment : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentLanguageBottomSheetBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

                bottomSheet.findViewById<Button>(R.id.save_btn_lang).setOnClickListener {
                    val languageOptions = bottomSheet.findViewById<RadioGroup>(R.id.lang_radio_group)
                    val selectedLanguageIndex = languageOptions.checkedRadioButtonId
                    when (selectedLanguageIndex) {
                        R.id.en -> setLocale("en")
                        R.id.it -> setLocale("it")
                        R.id.ru -> setLocale("ru")
                        R.id.uz -> setLocale("uz")
                    }
                    requireActivity().recreate()
                    dialog?.dismiss()
                }
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    private fun setLocale(lang : String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)

        val editor = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("lang", lang)
        editor.apply()
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}

