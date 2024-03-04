package com.example.twitturin.follow.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentFollowersBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.auth.model.data.User
import com.example.twitturin.follow.presentation.adapters.FollowersAdapter
import com.example.twitturin.follow.vm.FollowViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random
import javax.inject.Inject

@AndroidEntryPoint
class FollowersFragment : Fragment() {

    @Inject lateinit var sessionManager : SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private val followViewModel: FollowViewModel by viewModels()
    private val binding by lazy { FragmentFollowersBinding.inflate(layoutInflater) }
    private val followersAdapter by lazy { FollowersAdapter(viewLifecycleOwner, followViewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.followersFragment = this

        binding.anViewFollowers.setFailureListener { t ->
            snackbarHelper.snackbarError(
                requireActivity().findViewById(R.id.followers_root_layout),
                binding.anchorTv,
                error = t.message.toString(),
                ""){}
        }

        updateRecyclerView()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.rcViewFollowers.adapter = followersAdapter
        binding.rcViewFollowers.addItemDecoration(DividerItemDecoration(binding.rcViewFollowers.context, DividerItemDecoration.VERTICAL))
        binding.rcViewFollowers.layoutManager = LinearLayoutManager(requireContext())
        val userId = sessionManager.getUserId()
        followViewModel.getFollowers(userId!!)
        followViewModel.followersList.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<User> = tweets.toMutableList()
                    followersAdapter.setData(tweetList)
                    binding.swipeToRefreshLayoutFollowersList.setOnRefreshListener {

                        tweetList.shuffle(Random(System.currentTimeMillis()))
                        followViewModel.getFollowers(userId)
                        binding.swipeToRefreshLayoutFollowersList.isRefreshing = false

                    }

                    if (tweetList.isEmpty()) {

                        binding.rcViewFollowers.visibility = View.GONE
                        binding.anViewFollowers.visibility = View.VISIBLE
                        binding.emptyFollowersTv.visibility = View.VISIBLE

                    } else {

                        binding.rcViewFollowers.visibility = View.VISIBLE
                        binding.anViewFollowers.visibility = View.GONE
                        binding.emptyFollowersTv.visibility = View.GONE
                        followersAdapter.setData(tweetList)
                    }
                }
            } else {
                snackbarHelper.snackbarError(
                    requireActivity().findViewById(R.id.followers_root_layout),
                    binding.anchorTv,
                    error = response.body().toString(),
                    ""){}
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FollowersFragment()
    }
}