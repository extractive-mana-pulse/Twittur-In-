package com.example.twitturin.tweet.presentation.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.beVisibleIf
import com.example.twitturin.core.extensions.sendEmail
import com.example.twitturin.core.extensions.sharedPreferences
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.databinding.FragmentReportBinding

class ReportFragment : Fragment() {

    private val binding  by lazy { FragmentReportBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subject by requireActivity().sharedPreferences("subject_of_report")
        val context by requireActivity().sharedPreferences("context_of_report")

        binding.apply {

            reportPageToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            radioSpam.setOnCheckedChangeListener { _, _ -> }

            radioPrivacy.setOnCheckedChangeListener{ _, _ -> }

            radioAbuse.setOnCheckedChangeListener { _, _ -> }

            radioOther.setOnCheckedChangeListener { _, isChecked -> describeReportEt.beVisibleIf(isChecked) }

            reportNextBtn.setOnClickListener {
                sendEmail(subject, context)
                reportNextBtn.snackbar(reportNextBtn, R.string.gratitude.toString())
                findNavController().navigate(R.id.action_reportFragment_to_homeFragment)
            }
            setupRadioButtons()
        }
    }

    private fun setupRadioButtons() {
        binding.apply {
            radioSpam.setOnCheckedChangeListener { _, isChecked ->
                updateButtonState(isChecked, radioPrivacy.isChecked, radioAbuse.isChecked, radioOther.isChecked)
            }
            radioPrivacy.setOnCheckedChangeListener { _, isChecked ->
                updateButtonState(radioSpam.isChecked, isChecked, radioAbuse.isChecked, radioOther.isChecked)
            }
            radioAbuse.setOnCheckedChangeListener { _, isChecked ->
                updateButtonState(radioSpam.isChecked, radioPrivacy.isChecked, isChecked, radioOther.isChecked)
            }

            radioOther.setOnCheckedChangeListener { _, isChecked ->
                updateButtonState(radioSpam.isChecked, radioPrivacy.isChecked, radioAbuse.isChecked, isChecked)
            }
            updateButtonState(radioSpam.isChecked, radioPrivacy.isChecked, radioAbuse.isChecked, radioOther.isChecked)
        }
    }

    private fun updateButtonState(isSpamChecked: Boolean, isPrivacyChecked: Boolean, isAbuseChecked: Boolean, isOtherChecked: Boolean) {
        binding.apply {
            reportNextBtn.isEnabled = isSpamChecked || isPrivacyChecked || isAbuseChecked || isOtherChecked
            describeReportEt.beVisibleIf(binding.radioOther.isChecked)

            var subject by requireActivity().sharedPreferences("subject_of_report")
            var context by requireActivity().sharedPreferences("context_of_report")

            if (isSpamChecked) {
                subject = radioSpam.text.toString()
                context = spamDescTv.text.toString()
            } else if (isPrivacyChecked) {
                subject = radioPrivacy.text.toString()
                context = privacyDescTv.text.toString()
            } else if (isAbuseChecked) {
                subject = radioAbuse.text.toString()
                context = abuseAndHarassmentDescTv.text.toString()
            } else if (isOtherChecked) {
                subject = radioOther.text.toString()
                context = describeReportEt.text.toString()
            }
        }
    }
}