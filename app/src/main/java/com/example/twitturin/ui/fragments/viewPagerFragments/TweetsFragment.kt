package com.example.twitturin.ui.fragments.viewPagerFragments

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.SessionManager
import com.example.twitturin.databinding.FragmentTweetsBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.ui.adapters.TweetsAdapter
import com.example.twitturin.ui.sealeds.UserTweetsResult
import com.example.twitturin.viewmodel.ProfileViewModel
import java.util.Random

class TweetsFragment : Fragment() {

    private lateinit var binding: FragmentTweetsBinding

    private val tweetsAdapter by lazy { TweetsAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTweetsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.anView.setFailureListener { t ->
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            Log.d("Lottie", t.message.toString())
        }
        binding.anView.setAnimation(R.raw.empty_tweets_list)

        binding.createTweetTv.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_publicPostFragment)
        }
        binding.createTweetTv.paintFlags = binding.createTweetTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        recyclerViewSetup()
        updateRecyclerView()
    }

    private fun recyclerViewSetup(){
        binding.rcView.adapter = tweetsAdapter
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        val viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        val token = sessionManager.getToken()
        val adapter = TweetsAdapter()

        viewModel.data.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UserTweetsResult.Success -> {
                    val tweets = result.tweets
                    adapter.setData(tweets)
                }

                is UserTweetsResult.Failure -> {
                    val exception = result.exception
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                    Log.d("Failure", exception.message.toString())
                }
            }
        }
        viewModel.getPostsByUser(userId!!, "Bearer $token")
    }


    companion object {
        @JvmStatic
        fun newInstance() = TweetsFragment()
    }
}