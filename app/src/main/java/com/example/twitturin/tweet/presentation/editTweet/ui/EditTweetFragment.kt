package com.example.twitturin.tweet.presentation.editTweet.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentEditTweetBinding
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.util.snackbarError
import com.example.twitturin.tweet.presentation.editTweet.sealed.EditTweet
import com.example.twitturin.tweet.presentation.editTweet.vm.EditTweetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTweetFragment : Fragment() {

    private val editTweetViewModel: EditTweetViewModel by viewModels()
    private val binding by lazy { FragmentEditTweetBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tweetFragment = this

        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val tweetId = sharedPreferences.getString("id",null)

        val testTweetId = arguments?.getString("id")
        val tweetContent = arguments?.getString("description")

        binding.apply {

            editTweetContent.setText(tweetContent)

            editTweetPublishBtn.setOnClickListener {
                editTweetPublishBtn.isEnabled = false
                val content = editTweetContent.text.toString()
                editTweetViewModel.editTweet(content, tweetId!!, "Bearer ${SessionManager(requireContext()).getToken()}")
            }

            editTweetViewModel.editTweetResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is EditTweet.Success -> { findNavController().navigateUp() }

                    is EditTweet.Error -> {
                        binding.editTweetPageRootLayout.snackbarError(
                            requireActivity().findViewById<ConstraintLayout>(R.id.edit_tweet_tv_for_snackbar),
                            error = result.error,
                            ""){}
                        editTweetPublishBtn.isEnabled = true
                    }
                }
            }
        }
    }
}