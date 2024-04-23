package com.example.twitturin.follow.presentation.followers.ui

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
import com.example.twitturin.follow.domain.model.FollowUser
import com.example.twitturin.follow.presentation.followers.adapter.FollowersAdapter
import com.example.twitturin.follow.presentation.followers.vm.FollowersUiViewModel
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.util.snackbarError
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random

@AndroidEntryPoint
class FollowersFragment : Fragment() {

    private val followViewModel: FollowViewModel by viewModels()
    private val followersUiViewModel : FollowersUiViewModel by viewModels()
    private val binding by lazy { FragmentFollowersBinding.inflate(layoutInflater) }
    private val followersAdapter by lazy { FollowersAdapter(viewLifecycleOwner, followViewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.followersFragment = this
        updateRecyclerView()
        followersUiViewModel
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.apply {

            rcViewFollowers.adapter = followersAdapter
            rcViewFollowers.addItemDecoration(DividerItemDecoration(rcViewFollowers.context, DividerItemDecoration.VERTICAL))
            rcViewFollowers.layoutManager = LinearLayoutManager(requireContext())
            val userId = SessionManager(requireContext()).getUserId()
            followViewModel.getFollowers(userId!!)
            followViewModel.followersList.observe(requireActivity()) { response ->
                if (response.isSuccessful) {
                    response.body()?.let { tweets ->
                        val followersList: MutableList<FollowUser> = tweets.toMutableList()
                        followersAdapter.differ.submitList(followersList)
                        swipeToRefreshLayoutFollowersList.setOnRefreshListener {

                            followersList.shuffle(Random(System.currentTimeMillis()))
                            followViewModel.getFollowers(userId)
                            swipeToRefreshLayoutFollowersList.isRefreshing = false

                        }

                        if (followersList.isEmpty()) {

                            rcViewFollowers.visibility = View.GONE
                            anViewFollowers.visibility = View.VISIBLE
                            emptyFollowersTv.visibility = View.VISIBLE

                        } else {

                            rcViewFollowers.visibility = View.VISIBLE
                            anViewFollowers.visibility = View.GONE
                            emptyFollowersTv.visibility = View.GONE
                            followersAdapter.differ.submitList(followersList)
                        }
                    }
                } else {
                    followersRootLayout.snackbarError(
                        requireActivity().findViewById(R.id.followers_root_layout),
                        error = response.body().toString(),
                        ""
                    ) {}
                }
            }
        }
    }
}