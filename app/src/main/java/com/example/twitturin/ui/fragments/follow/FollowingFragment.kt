package com.example.twitturin.ui.fragments.follow

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentFollowingBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.FollowingAdapter
import com.example.twitturin.viewmodel.FollowingViewModel
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class FollowingFragment : Fragment() {

    private lateinit var viewModel : MainViewModel
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private lateinit var binding : FragmentFollowingBinding
    private val fViewModel: FollowingViewModel by viewModels()
    private val followingAdapter by lazy { FollowingAdapter(viewLifecycleOwner, fViewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFollowingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.anViewFollowing.setFailureListener { t ->
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
        }

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]

        binding.backBtnFollowingList.setOnClickListener {
            requireActivity().onBackPressed()
        }
        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.rcViewFollowing.adapter = followingAdapter
        binding.rcViewFollowing.addItemDecoration(DividerItemDecoration(binding.rcViewFollowing.context, DividerItemDecoration.VERTICAL))
        binding.rcViewFollowing.layoutManager = LinearLayoutManager(requireContext())
        val userId = sessionManager.getUserId()
        viewModel.getFollowing(userId!!)
        viewModel.followingList.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<User> = tweets.toMutableList()
                    followingAdapter.setData(tweetList)
                    binding.swipeToRefreshLayoutFollowingList.setOnRefreshListener {
                        tweetList.shuffle(Random(System.currentTimeMillis()))
                        viewModel.getFollowing(userId)
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
                    requireActivity().findViewById(R.id.following_root_layout1),
                    requireActivity().findViewById(R.id.following_root_layout1),
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