package com.example.twitturin.ui.fragments.viewPager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentTweetsBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.UserPostAdapter
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager
import com.google.android.material.snackbar.Snackbar
import java.util.Random

class TweetsFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentTweetsBinding
    private val userPostAdapter by lazy { UserPostAdapter(viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTweetsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.anView.setFailureListener { t ->
            snackbarError(t.message.toString())
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
                snackbarError(response.body().toString())
            }
        }
    }

    private fun snackbarError(error : String) {
        val rootView = view?.findViewById<ConstraintLayout>(R.id.tweets_root_layout)
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = Snackbar
            .make(rootView!!, error, duration)
            .setBackgroundTint(resources.getColor(R.color.md_theme_light_errorContainer))
            .setTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
            .setActionTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
        snackbar.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = TweetsFragment()
    }
}