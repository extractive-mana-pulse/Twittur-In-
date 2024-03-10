package com.example.twitturin.tweet.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentPublicPostBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.tweet.sealed.PostTweet
import com.example.twitturin.tweet.vm.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PublicPostFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private val tweetViewModel : TweetViewModel by viewModels()
    private val binding by lazy { FragmentPublicPostBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.publicPostFragment  = this

        val token = sessionManager.getToken()

        binding.apply {
            btnTweet.setOnClickListener {
                btnTweet.isEnabled = false
                val tweetContent = contentEt.text.toString()
                tweetViewModel.postTheTweet(tweetContent, "Bearer $token")
            }

            tweetViewModel.postTweetResult.observe(viewLifecycleOwner) { result ->

                when (result) {
                    is PostTweet.Success -> {
                        findNavController().navigate(R.id.action_publicPostFragment_to_homeFragment)
                    }

                    is PostTweet.Error -> {
                        snackbarHelper.snackbarError(
                            requireActivity().findViewById(R.id.public_post_root_layout),
                            requireActivity().findViewById(R.id.public_post_root_layout),
                            error = result.message,
                            ""){}
                        btnTweet.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.md_theme_light_surface)
    }

    override fun onPause() {
        super.onPause()
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), com.google.android.material.R.color.m3_sys_color_light_surface_container)
    }
}