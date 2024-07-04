package com.example.twitturin.tweet.presentation.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentReportBinding
import com.google.android.material.snackbar.Snackbar

class ReportFragment : Fragment() {

    private val binding  by lazy { FragmentReportBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            reportPageToolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            radioSpam.setOnCheckedChangeListener { _, _ -> /* TODO: hande click and send content to server. */ }

            radioPrivacy.setOnCheckedChangeListener{ _, _ -> /* TODO: hande click and send content to server. */ }

            radioAbuse.setOnCheckedChangeListener { _, _ -> /* TODO: hande click and send content to server. */ }

            radioOther.setOnCheckedChangeListener { _, isChecked ->
                describeReportEt.visibility = if (isChecked) View.VISIBLE else View.GONE
            }

            reportNextBtn.setOnClickListener {
                Snackbar.make(reportNextBtn, R.string.gratitude, Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_reportFragment_to_homeFragment)
            }
            setupRadioButtons()
        }
    }

    private fun setupRadioButtons() {
        // Get references to the radio buttons
        binding.apply {
            // Set up the radio button click listeners
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
                updateButtonState(radioSpam.isChecked, radioPrivacy.isChecked, radioOther.isChecked, isChecked)
            }

            // Initially update the button state
            updateButtonState(radioSpam.isChecked, radioPrivacy.isChecked, radioAbuse.isChecked, radioOther.isChecked)
        }
    }

    private fun updateButtonState(isSpamChecked: Boolean, isPrivacyChecked: Boolean, isAbuseChecked: Boolean, isOtherChecked: Boolean) {
        // Enable or disable the button based on whether any radio button is checked
        binding.reportNextBtn.isEnabled = isSpamChecked || isPrivacyChecked || isAbuseChecked || isOtherChecked
        binding.describeReportEt.visibility = if (binding.radioOther.isChecked) View.VISIBLE else View.GONE
    }
}