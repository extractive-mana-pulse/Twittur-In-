package com.example.twitturin.follow.presentation.following.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.core.extensions.beVisible
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.extensions.vertical
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentFollowingBinding
import com.example.twitturin.follow.domain.model.FollowUser
import com.example.twitturin.follow.presentation.following.adapter.FollowingAdapter
import com.example.twitturin.follow.presentation.following.adapter.FollowingAdapter.FollowingClickEvent
import com.example.twitturin.follow.presentation.following.sealed.UnFollow
import com.example.twitturin.follow.presentation.following.vm.FollowingUiViewModel
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowingFragment : Fragment() {

    private val followViewModel: FollowViewModel by viewModels()
    private val followingViewModel : FollowingUiViewModel by viewModels()
    private val binding by lazy { FragmentFollowingBinding.inflate(layoutInflater)}
    private val followingAdapter by lazy { FollowingAdapter(clickEvent = ::handleClickEvent) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.followingToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        binding.apply {

            rcViewFollowing.vertical().adapter = followingAdapter
            followingAdapter.notifyItemChanged(followingAdapter.differ.currentList.size)

            followViewModel.getFollowing(SessionManager(requireContext()).getUserId()!!)

            followViewModel.followingList.observe(requireActivity()) { response ->

                if (response.isSuccessful) {

                    response.body()?.let { following ->

                        val followingList: MutableList<FollowUser> = following.toMutableList()
                        followingAdapter.differ.submitList(followingList)

                        swipeToRefreshLayoutFollowingList.setOnRefreshListener {
                            followViewModel.getFollowing(SessionManager(requireContext()).getUserId()!!)
                            swipeToRefreshLayoutFollowingList.isRefreshing = false
                        }

                        if (followingList.isEmpty()){
                            rcViewFollowing.beGone()
                            anViewFollowing.beVisible()
                            emptyFollowingTv.beVisible()
                        } else {
                            rcViewFollowing.beVisible()
                            anViewFollowing.beGone()
                            emptyFollowingTv.beGone()
                            followingAdapter.differ.submitList(followingList)
                        }
                    }
                } else {
                    root.snackbarError(snackbarView, response.message(), ""){}
                }
            }
        }
    }

    private fun handleClickEvent(clickEvent: FollowingClickEvent, followUser: FollowUser) {
        when(clickEvent) {

            FollowingClickEvent.ITEM -> { findNavController().navigate(R.id.observeProfileFragment) }

            FollowingClickEvent.UNFOLLOW -> {

                followViewModel.unFollowUser(followUser.id!!, "Bearer ${SessionManager(requireContext()).getToken()}")
                followViewModel.unFollow.observe(viewLifecycleOwner) { response ->
                    when(response) {
                        is UnFollow.Success -> { Toast.makeText(requireContext(), "${response.user.username}", Toast.LENGTH_SHORT).show() }
                        is UnFollow.Error -> { binding.root.snackbarError(binding.snackbarView, response.message, "") {} }
                        is UnFollow.Loading -> {}
                    }
                }
            }
        }
    }
}