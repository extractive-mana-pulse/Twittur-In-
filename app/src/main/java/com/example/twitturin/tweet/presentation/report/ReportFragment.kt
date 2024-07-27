package com.example.twitturin.tweet.presentation.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.beVisibleIf
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.databinding.FragmentReportBinding

class ReportFragment : Fragment() {

    private val binding  by lazy { FragmentReportBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            reportPageToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            radioSpam.setOnCheckedChangeListener { _, _ -> /* TODO: hande click and send content to server. */ }

            radioPrivacy.setOnCheckedChangeListener{ _, _ -> /* TODO: hande click and send content to server. */ }

            radioAbuse.setOnCheckedChangeListener { _, _ -> /* TODO: hande click and send content to server. */ }

            radioOther.setOnCheckedChangeListener { _, isChecked -> describeReportEt.beVisibleIf(isChecked) }

            reportNextBtn.setOnClickListener {
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
        binding.reportNextBtn.isEnabled = isSpamChecked || isPrivacyChecked || isAbuseChecked || isOtherChecked
        binding.describeReportEt.beVisibleIf(binding.radioOther.isChecked)
    }
}