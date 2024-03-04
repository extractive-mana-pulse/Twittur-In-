package com.example.twitturin.follow.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentFollowingBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.auth.model.data.User
import com.example.twitturin.follow.presentation.adapters.FollowingAdapter
import com.example.twitturin.follow.vm.FollowViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random
import javax.inject.Inject

@AndroidEntryPoint
class FollowingFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private val followViewModel: FollowViewModel by viewModels()
    private val binding by lazy { FragmentFollowingBinding.inflate(layoutInflater)}
    private val followingAdapter by lazy { FollowingAdapter(viewLifecycleOwner, followViewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.followingFragment = this

        binding.anViewFollowing.setFailureListener { t ->
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
        }

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
                    val tweetList: MutableList<User> = tweets.toMutableList()
                    followingAdapter.setData(tweetList)
                    binding.swipeToRefreshLayoutFollowingList.setOnRefreshListener {
                        tweetList.shuffle(Random(System.currentTimeMillis()))
                        followViewModel.getFollowing(userId)
                        binding.swipeToRefreshLayoutFollowingList.isRefreshing = false
                    }

                    if (tweetList.isEmpty()){
                        binding.rcViewFollowing.visibility = View.GONE
                        binding.anViewFollowing.visibility = View.VISIBLE
                        binding.emptyFollowingTv.visibility = View.VISIBLE
                    } else {
                        binding.rcViewFollowing.visibility = View.VISIBLE
                        binding.anViewFollowing.visibility = View.GONE
                        binding.emptyFollowingTv.visibility = View.GONE

                        followingAdapter.setData(tweetList)
                    }
                }
            } else {
                snackbarHelper.snackbarError(
                    requireActivity().findViewById(R.id.following_root_layout),
                    requireActivity().findViewById(R.id.following_root_layout),
                    error = response.body().toString(),
                    ""){}
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FollowingFragment()
    }
}