package com.example.twitturin.tweet.presentation.fragments

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
import com.example.twitturin.databinding.FragmentLikesBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.tweet.model.data.Tweet
import com.example.twitturin.tweet.vm.TweetViewModel
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.tweet.vm.LikeViewModel
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LikesFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding : FragmentLikesBinding
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private val lViewModel: LikeViewModel by viewModels()
    private val tweetViewModel : TweetViewModel by viewModels()
    private val postAdapter by lazy { PostAdapter(lViewModel, viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLikesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.likesPageAnView.setFailureListener { t ->
            snackbarHelper.snackbarError(
                requireActivity().findViewById(R.id.likes_root_layout),
                requireActivity().findViewById(R.id.likes_root_layout),
                error = t.message.toString(),
                ""){}

        }

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        updateRecyclerView()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        val userId = sessionManager.getUserId()
        binding.rcView.adapter = postAdapter
        binding.rcView.addItemDecoration(DividerItemDecoration(binding.rcView.context, DividerItemDecoration.VERTICAL))
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        tweetViewModel.getLikedPosts(userId!!)
        tweetViewModel.likedPosts.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<Tweet> = tweets.toMutableList()
                    if (tweetList.isEmpty()) {
                        binding.rcView.visibility = View.GONE
                        binding.likesPageAnView.visibility = View.VISIBLE
                        binding.lottieInfoTv.visibility = View.VISIBLE
                    } else {
                        binding.rcView.visibility = View.VISIBLE
                        binding.likesPageAnView.visibility = View.GONE
                        binding.lottieInfoTv.visibility = View.GONE
                        postAdapter.setData(tweetList)
                        tweetViewModel.getLikedPosts(userId)
                    }
                }
            } else {
                snackbarHelper.snackbarError(
                    requireActivity().findViewById(R.id.likes_root_layout),
                    requireActivity().findViewById(R.id.likes_root_layout),
                    error = response.body().toString(),
                    ""){}
            }
        }
    }
}