package com.example.twitturin.tweet.presentation.fragments

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
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.tweet.presentation.sealed.EditTweet
import com.example.twitturin.tweet.presentation.vm.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditTweetFragment : Fragment() {

    @Inject lateinit var snackbarHelper: SnackbarHelper
    @Inject lateinit var sessionManager : SessionManager
    private val tweetViewModel: TweetViewModel by viewModels()
    private val binding by lazy { FragmentEditTweetBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
            val description = sharedPreferences.getString("description", "")

            val tweetId = sharedPreferences.getString("id","")
            val token = sessionManager.getToken()

            editTweetContent.setText(description)

            editTweetCancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            editTweetPublishBtn.setOnClickListener {
                editTweetPublishBtn.isEnabled = false
                val content = editTweetContent.text.toString()
                tweetViewModel.editTweet(content, tweetId!!, "Bearer $token")
            }

            tweetViewModel.editTweetResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is EditTweet.Success -> {
                        findNavController().popBackStack()
                    }

                    is EditTweet.Error -> {
                        snackbarHelper.snackbarError(
                            requireActivity().findViewById<ConstraintLayout>(R.id.edit_tweet_tv_for_snackbar),
                            editTweetTvForSnackbar,
                            error = result.error,
                            ""
                        ) {}
                        editTweetPublishBtn.isEnabled = true
                    }
                }
            }
        }
    }
}