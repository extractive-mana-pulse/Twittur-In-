package com.example.twitturin.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentPublicPostBinding
import com.example.twitturin.presentation.mvvm.MainViewModel
import com.example.twitturin.presentation.mvvm.Repository
import com.example.twitturin.presentation.mvvm.ViewModelFactory
import com.example.twitturin.presentation.sealeds.PostTweet


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

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]

        binding.btnTweet.setOnClickListener {
            val tweet = binding.contentEt.text.toString()
            viewModel.postTheTweet(tweet)
        }

        viewModel.postTweetResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is PostTweet.Success -> {
                    findNavController().navigate(R.id.action_publicPostFragment_to_homeFragment)
                    Toast.makeText(requireContext(), result.response.toString(), Toast.LENGTH_SHORT).show()
                }

                is PostTweet.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun cancelBtn(){
        requireActivity().onBackPressed()
    }

    fun tweetBtn(){
        // get logic from api to post a tweet
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicPostFragment()
    }
}