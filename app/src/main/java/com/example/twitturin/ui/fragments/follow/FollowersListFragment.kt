package com.example.twitturin.ui.fragments.follow

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentFollowersListBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.FollowersAdapter
import com.example.twitturin.viewmodel.FollowUserViewModel
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class FollowersListFragment : Fragment() {

    private lateinit var viewModel : MainViewModel
    @Inject lateinit var sessionManager : SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private lateinit var followViewModel: FollowUserViewModel
    private lateinit var binding : FragmentFollowersListBinding
    private val fViewModel: FollowUserViewModel by viewModels()
    private val followersAdapter by lazy { FollowersAdapter(viewLifecycleOwner, fViewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFollowersListBinding.inflate(layoutInflater)
        followViewModel = ViewModelProvider(this)[FollowUserViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.anViewFollowers.setFailureListener { t ->
            snackbarHelper.snackbarError(
                requireActivity().findViewById(R.id.followers_root_layout),
                binding.anchorTv,
                error = t.message.toString(),
                ""){}
        }

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]

        binding.backBtnFollowersList.setOnClickListener {
            requireActivity().onBackPressed()
        }
        updateRecyclerView()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.rcViewFollowers.adapter = followersAdapter
        binding.rcViewFollowers.addItemDecoration(DividerItemDecoration(binding.rcViewFollowers.context, DividerItemDecoration.VERTICAL))
        binding.rcViewFollowers.layoutManager = LinearLayoutManager(requireContext())
        val userId = sessionManager.getUserId()
        viewModel.getFollowers(userId!!)
        viewModel.followersList.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<User> = tweets.toMutableList()
                    followersAdapter.setData(tweetList)
                    binding.swipeToRefreshLayoutFollowersList.setOnRefreshListener {

                        tweetList.shuffle(Random(System.currentTimeMillis()))
                        viewModel.getFollowers(userId)
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
        fun newInstance() = FollowersListFragment()
    }
}