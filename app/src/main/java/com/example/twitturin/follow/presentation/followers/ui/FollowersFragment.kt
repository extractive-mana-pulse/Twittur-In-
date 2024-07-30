package com.example.twitturin.follow.presentation.followers.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.core.extensions.vertical
import com.example.twitturin.databinding.FragmentFollowersBinding
import com.example.twitturin.follow.domain.model.FollowUser
import com.example.twitturin.follow.presentation.followers.adapter.FollowersAdapter
import com.example.twitturin.follow.presentation.followers.adapter.FollowersAdapter.ClickEvent
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.followers.sealed.FollowersUiEvent
import com.example.twitturin.follow.presentation.followers.vm.FollowersUiViewModel
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.core.extensions.snackbarError
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Random

@AndroidEntryPoint
class FollowersFragment : Fragment() {

    private val followViewModel: FollowViewModel by viewModels()
    private val followersUiViewModel : FollowersUiViewModel by viewModels()
    private val binding by lazy { FragmentFollowersBinding.inflate(layoutInflater) }
    private val followersAdapter by lazy { FollowersAdapter(clickEvent = ::handleClickEvent) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.followersToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.apply {

            rcViewFollowers.vertical().adapter = followersAdapter

            followViewModel.getFollowers(SessionManager(requireContext()).getUserId()!!)

            followViewModel.followersList.observe(viewLifecycleOwner) { response ->

                if (response.isSuccessful) {

                    response.body()?.let { followers ->

                        val followersList: MutableList<FollowUser> = followers.toMutableList()
                        followersAdapter.differ.submitList(followersList)

                        swipeToRefreshLayoutFollowersList.setOnRefreshListener {
                            followersList.shuffle(Random(System.currentTimeMillis()))
                            followViewModel.getFollowers(SessionManager(requireContext()).getUserId()!!)
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
                        followersRootLayout,
                        error = response.message(),
                        ""
                    ) {}
                }
            }
        }
    }

    private fun handleClickEvent(clickEvent: ClickEvent, followUser: FollowUser) {
        when(clickEvent) {
            ClickEvent.FOLLOW -> { followersUiViewModel.followPressed() }
            ClickEvent.ITEM_SELECTED -> { followersUiViewModel.itemPressed() }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            followersUiViewModel.followersEvent.collect{
                when(it){
                    FollowersUiEvent.OnFollowPressed ->  {
                        followViewModel.followUsers(followUser.id!!,"Bearer ${SessionManager(requireContext()).getToken()}")
                        repeatOnStarted {
                            followViewModel.follow.collectLatest { result ->
                                when (result) {

                                    is Follow.Success -> { binding.followersRootLayout.snackbar(binding.followersRootLayout, "you follow: ${result.user.username.toString()}") }
                                    is Follow.Error -> { binding.followersRootLayout.snackbarError(binding.followersRootLayout, result.message, R.string.retry.toString()){} }
                                    Follow.Loading -> {}
                                }
                            }
                        }
                    }
                    FollowersUiEvent.OnItemPressed -> { findNavController().navigate(R.id.observeProfileFragment) }
                }
            }
        }
    }
}