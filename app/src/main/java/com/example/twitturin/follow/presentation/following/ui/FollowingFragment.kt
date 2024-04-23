package com.example.twitturin.follow.presentation.following.ui

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
import com.example.twitturin.databinding.FragmentFollowingBinding
import com.example.twitturin.follow.domain.model.FollowUser
import com.example.twitturin.follow.presentation.following.adapter.FollowingAdapter
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.util.snackbarError
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random
import javax.inject.Inject

@AndroidEntryPoint
class FollowingFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    private val followViewModel: FollowViewModel by viewModels()
    private val binding by lazy { FragmentFollowingBinding.inflate(layoutInflater)}
    private val followingAdapter by lazy { FollowingAdapter(viewLifecycleOwner, followViewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.followingFragment = this
        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.rcViewFollowing.adapter = followingAdapter
        binding.rcViewFollowing.addItemDecoration(DividerItemDecoration(binding.rcViewFollowing.context, DividerItemDecoration.VERTICAL))
        binding.rcViewFollowing.layoutManager = LinearLayoutManager(requireContext())
        val userId = sessionManager.getUserId()
        followViewModel.getFollowing(userId!!)
        followViewModel.followingList.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val followingList: MutableList<FollowUser> = tweets.toMutableList()
                    followingAdapter.differ.submitList(followingList)
                    binding.swipeToRefreshLayoutFollowingList.setOnRefreshListener {
                        followingList.shuffle(Random(System.currentTimeMillis()))
                        followViewModel.getFollowing(userId)
                        binding.swipeToRefreshLayoutFollowingList.isRefreshing = false
                    }

                    if (followingList.isEmpty()){
                        binding.rcViewFollowing.visibility = View.GONE
                        binding.anViewFollowing.visibility = View.VISIBLE
                        binding.emptyFollowingTv.visibility = View.VISIBLE
                    } else {
                        binding.rcViewFollowing.visibility = View.VISIBLE
                        binding.anViewFollowing.visibility = View.GONE
                        binding.emptyFollowingTv.visibility = View.GONE
                        followingAdapter.differ.submitList(followingList)
                    }
                }
            } else {
                binding.followingRootLayout.snackbarError(
                    requireActivity().findViewById(R.id.followers_root_layout),
                    error = response.body().toString(),
                    ""){}
            }
        }
    }
}