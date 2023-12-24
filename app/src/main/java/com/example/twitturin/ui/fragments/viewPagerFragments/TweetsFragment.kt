package com.example.twitturin.ui.fragments.viewPagerFragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentTweetsBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager
import java.util.Random

class TweetsFragment : Fragment() {

    private lateinit var binding: FragmentTweetsBinding
    private lateinit var viewModel: MainViewModel
    private val postAdapter by lazy { PostAdapter(viewLifecycleOwner) }

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

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        binding.rcView.adapter = postAdapter
        binding.rcView.addItemDecoration(DividerItemDecoration(binding.rcView.context, DividerItemDecoration.VERTICAL))
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getUserTweet(userId!!)
        viewModel.userTweets.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<Tweet> = tweets.toMutableList()
                    if (tweetList.isEmpty()) {
                        binding.rcView.visibility = View.GONE
                        binding.anView.visibility = View.VISIBLE
                        binding.lottieInfoTv.visibility = View.VISIBLE
                    } else {
                        binding.rcView.visibility = View.VISIBLE
                        binding.anView.visibility = View.GONE
                        binding.lottieInfoTv.visibility = View.GONE
                        postAdapter.setData(tweetList)
                        postAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                Toast.makeText(requireContext(), response.code().toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TweetsFragment()
    }
}