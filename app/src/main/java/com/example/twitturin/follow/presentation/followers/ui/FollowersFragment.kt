package com.example.twitturin.follow.presentation.followers.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.core.extensions.beVisible
import com.example.twitturin.core.extensions.showCustomSnackbar
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.extensions.vertical
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentFollowersBinding
import com.example.twitturin.follow.domain.model.FollowUser
import com.example.twitturin.follow.presentation.followers.adapter.FollowersAdapter
import com.example.twitturin.follow.presentation.followers.adapter.FollowersAdapter.FollowersClickEvent
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.followers.vm.FollowersUiViewModel
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import dagger.hilt.android.AndroidEntryPoint

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
                            followersAdapter.notifyItemRemoved(followersAdapter.differ.currentList.size)
                            swipeToRefreshLayoutFollowersList.isRefreshing = false
                        }

                        if (followersList.isEmpty()) {
                            rcViewFollowers.beGone()
                            anViewFollowers.beVisible()
                            emptyFollowersTv.beVisible()

                        } else {
                            rcViewFollowers.beVisible()
                            anViewFollowers.beGone()
                            emptyFollowersTv.beGone()
                            followersAdapter.differ.submitList(followersList)
                        }
                    }
                } else {
                    root.snackbarError(snackbarView, response.message(), "") {}
                }
            }
        }
    }

    private fun handleClickEvent(clickEvent: FollowersClickEvent, followUser: FollowUser) {
        when(clickEvent) {
            FollowersClickEvent.ITEM_SELECTED -> { findNavController().navigate(R.id.observeProfileFragment) }

            FollowersClickEvent.FOLLOW -> {
                followViewModel.followUser(followUser.id!!,"Bearer ${SessionManager(requireContext()).getToken()}")
                followViewModel.follow.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Follow.Success -> { binding.root.showCustomSnackbar(binding.snackbarView, requireContext(), "you follow: ${result.user.username}") }
                        is Follow.Error -> { binding.root.snackbarError(binding.snackbarView, result.message, ""){} }
                        Follow.Loading -> {}
                    }
                }
            }
        }
    }
}