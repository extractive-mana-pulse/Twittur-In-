package com.example.twitturin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentPublicPostBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.sealeds.PostTweet
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class PublicPostFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private lateinit var binding : FragmentPublicPostBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPublicPostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.publicPostFragment  = this

        val token = sessionManager.getToken()

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]

        binding.btnTweet.setOnClickListener {
                binding.btnTweet.isEnabled = false
                val tweetContent = binding.contentEt.text.toString()
                viewModel.postTheTweet(tweetContent, "Bearer $token")
        }

        viewModel.postTweetResult.observe(viewLifecycleOwner) { result ->

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
                    binding.btnTweet.isEnabled = true
                }
            }
        }
    }

    fun cancelBtn(){
        requireActivity().onBackPressed()
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicPostFragment()
    }
}