package com.example.twitturin.ui.fragments.viewPager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentTweetsBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.UserPostAdapter
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random
import javax.inject.Inject

@AndroidEntryPoint
class TweetsFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    @Inject lateinit var snackbarHelper: SnackbarHelper
    @Inject lateinit var sessionManager: SessionManager
    private lateinit var binding: FragmentTweetsBinding
    private val userPostAdapter by lazy { UserPostAdapter(viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTweetsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.anView.setFailureListener { t ->
            snackbarHelper.snackbarError(
                requireActivity().findViewById(R.id.tweets_root_layout),
                requireActivity().findViewById(R.id.tweets_root_layout),
                error = t.message.toString(),
                ""){}

        }
        binding.anView.setAnimation(R.raw.empty_tweets_list)

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {

        val userId = sessionManager.getUserId()
        viewModel.getUserTweet(userId!!)
        binding.rcView.adapter = userPostAdapter
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        binding.rcView.addItemDecoration(DividerItemDecoration(binding.rcView.context, DividerItemDecoration.VERTICAL))

        viewModel.userTweets.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->

                    val tweetList: MutableList<Tweet> = tweets.toMutableList()
                    binding.swipeToRefreshLayoutTweets.setOnRefreshListener {

                        viewModel.getUserTweet(userId)
                        userPostAdapter.notifyDataSetChanged()
                        tweetList.shuffle(Random(System.currentTimeMillis()))
                        binding.swipeToRefreshLayoutTweets.isRefreshing = false

                    }

                    if (tweetList.isEmpty()) {

                        binding.rcView.visibility = View.GONE
                        binding.anView.visibility = View.VISIBLE
                        binding.lottieInfoTv.visibility = View.VISIBLE

                    } else {

                        binding.rcView.visibility = View.VISIBLE
                        binding.anView.visibility = View.GONE
                        binding.lottieInfoTv.visibility = View.GONE
                        userPostAdapter.setData(tweetList)
                        userPostAdapter.notifyDataSetChanged()

                    }
                }

            } else {
                snackbarHelper.snackbarError(
                    requireActivity().findViewById(R.id.tweets_root_layout),
                    requireActivity().findViewById(R.id.tweets_root_layout),
                    error = response.body().toString(),
                    ""){}

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TweetsFragment()
    }
}