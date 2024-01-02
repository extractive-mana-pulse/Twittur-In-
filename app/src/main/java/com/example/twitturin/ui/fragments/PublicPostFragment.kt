package com.example.twitturin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.viewmodel.manager.SessionManager
import com.example.twitturin.databinding.FragmentPublicPostBinding
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.ui.sealeds.PostTweet


class PublicPostFragment : Fragment() {

    private lateinit var binding : FragmentPublicPostBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPublicPostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.publicPostFragment  = this

        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.getToken()
        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]

        binding.btnTweet.setOnClickListener {
            if (!token.isNullOrEmpty()){
                binding.btnTweet.isEnabled = false
                val tweetContent = binding.contentEt.text.toString()
                viewModel.postTheTweet(tweetContent, token)
            } else {
                Toast.makeText(requireContext(), "something went wrong my G", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.postTweetResult.observe(viewLifecycleOwner) { result ->

            when (result) {
                is PostTweet.Success -> {
                    findNavController().navigate(R.id.action_publicPostFragment_to_homeFragment)
                }

                is PostTweet.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
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