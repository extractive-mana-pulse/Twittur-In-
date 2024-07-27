package com.example.twitturin.feedback.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.core.extensions.reportSpinner
import com.example.twitturin.core.extensions.sendEmail
import com.example.twitturin.databinding.FragmentFeedbackBinding
import com.example.twitturin.feedback.presentation.sealed.FeedbackUIEvent
import com.example.twitturin.feedback.presentation.vm.FeedbackViewModel
import kotlinx.coroutines.launch

class FeedbackFragment : Fragment() {

    private val feedbackViewModel by viewModels<FeedbackViewModel>()
    private val binding by lazy { FragmentFeedbackBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            feedbackBackBtn.setOnClickListener { feedbackViewModel.onBackPressed() }
            feedbackSendEmailBtn.setOnClickListener {  feedbackViewModel.onSendPressed() }

            viewLifecycleOwner.lifecycleScope.launch {
                feedbackViewModel.feedbackUiEvent.collect {
                    when(it) {
                        FeedbackUIEvent.OnBackPressed -> { findNavController().navigateUp() }
                        FeedbackUIEvent.OnSendPressed -> { sendEmail(feedbackSpinner.selectedItem.toString(), messageFeedbackEt.text.toString().trim()) }
                    }
                }
            }
            feedbackSpinner.reportSpinner(requireContext(), feedbackSpinner, feedbackTopicLayout2)
        }
    }
}