package com.example.twitturin.follow.presentation.following.ui

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
import com.example.twitturin.core.extensions.vertical
import com.example.twitturin.databinding.FragmentFollowingBinding
import com.example.twitturin.follow.domain.model.FollowUser
import com.example.twitturin.follow.presentation.following.adapter.FollowingAdapter
import com.example.twitturin.follow.presentation.following.sealed.FollowingUIEvent
import com.example.twitturin.follow.presentation.following.sealed.UnFollow
import com.example.twitturin.follow.presentation.following.vm.FollowingUiViewModel
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.core.extensions.snackbarError
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Random

@AndroidEntryPoint
class FollowingFragment : Fragment() {

    private val followViewModel: FollowViewModel by viewModels()
    private val followingViewModel : FollowingUiViewModel by viewModels()
    private val binding by lazy { FragmentFollowingBinding.inflate(layoutInflater)}
    private val followingAdapter by lazy { FollowingAdapter(clickEvent = ::handleClickEvent) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.followingToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.apply {

            rcViewFollowing.vertical().adapter = followingAdapter

            followViewModel.getFollowing(SessionManager(requireContext()).getUserId()!!)

            followViewModel.followingList.observe(requireActivity()) { response ->

                if (response.isSuccessful) {

                    response.body()?.let { following ->
                        val followingList: MutableList<FollowUser> = following.toMutableList()
                        followingAdapter.differ.submitList(followingList)

                        binding.swipeToRefreshLayoutFollowingList.setOnRefreshListener {
                            followingList.shuffle(Random(System.currentTimeMillis()))
                            followViewModel.getFollowing(SessionManager(requireContext()).getUserId()!!)
                            swipeToRefreshLayoutFollowingList.isRefreshing = false
                        }

                        if (followingList.isEmpty()){
                            rcViewFollowing.visibility = View.GONE
                            anViewFollowing.visibility = View.VISIBLE
                            emptyFollowingTv.visibility = View.VISIBLE
                        } else {
                            rcViewFollowing.visibility = View.VISIBLE
                            anViewFollowing.visibility = View.GONE
                            emptyFollowingTv.visibility = View.GONE
                            followingAdapter.differ.submitList(followingList)
                        }
                    }
                } else {
                    followingRootLayout.snackbarError(
                        followingRootLayout,
                        error = response.message().toString(),
                        ""){}
                }
            }
        }
    }

    private fun handleClickEvent(clickEvent: FollowingAdapter.FollowingClickEvent, followUser: FollowUser) {
        when(clickEvent) {

            FollowingAdapter.FollowingClickEvent.UNFOLLOW -> { followingViewModel.onUnfollowPressed() }

            FollowingAdapter.FollowingClickEvent.ITEM -> { followingViewModel.onItemPressed() }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            followingViewModel.channel.collect {

                when(it){
                    FollowingUIEvent.OnItemPressed -> { findNavController().navigate(R.id.observeProfileFragment) }
                    FollowingUIEvent.OnUnFollowPressed -> { followViewModel.unFollowUser(followUser.id!!, "${SessionManager(requireContext()).getToken()}") }
                }

                followViewModel.unFollow.observe(viewLifecycleOwner) { response ->
                    when(response) {
                        is UnFollow.Success -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                followViewModel.sharedFlow.collect { username ->
                                    Snackbar.make(binding.followingRootLayout, "you unfollow $username", Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
                        is UnFollow.Error -> {
                            binding.followingRootLayout.snackbarError(
                                binding.followingRootLayout,
                                error = response.message,
                                actionText = R.string.retry.toString()
                            ) {}
                        }
                    }
                }
            }
        }
    }
}