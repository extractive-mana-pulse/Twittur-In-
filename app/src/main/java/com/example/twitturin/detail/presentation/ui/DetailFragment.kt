package com.example.twitturin.detail.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.core.extensions.addAutoResizeTextWatcher
import com.example.twitturin.core.extensions.convertDateFormat
import com.example.twitturin.core.extensions.formatCreatedAt
import com.example.twitturin.core.extensions.fullScreenImage
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.extensions.showKeyboard
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.extensions.vertical
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentDetailBinding
import com.example.twitturin.detail.presentation.sealed.DetailPageUI
import com.example.twitturin.detail.presentation.sealed.PostReply
import com.example.twitturin.detail.presentation.vm.DetailPageUIViewModel
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.home.presentation.adapter.HomeAdapter
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val tweetViewModel by viewModels<TweetViewModel>()
    private val followingViewModel by viewModels<FollowViewModel>()
    private val detailUiViewModel by viewModels<DetailPageUIViewModel>()
    private val binding  by lazy { FragmentDetailBinding.inflate(layoutInflater) }
    private val homeAdapter by lazy { HomeAdapter(homeClickEvents = ::homeClickEvent) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailFragment = this
        binding.apply {

            val id = arguments?.getString("id")
            val likes = arguments?.getString("likes")
            val userId = arguments?.getString("userId")
            val username = arguments?.getString("username")
            val updatedAt = arguments?.getString("updatedAt")
            val createdAt = arguments?.getString("createdAt")
            val userFullname = arguments?.getString("fullname")
            val profileImage = arguments?.getString("userAvatar")
            val postDescription = arguments?.getString("post_description")
            val activateEditText = arguments?.getBoolean("activateEditText", false)

            val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("post_description", postDescription).apply()
            sharedPreferences.edit().putString("userImage", profileImage).apply()
            sharedPreferences.edit().putString("username", username).apply()
            sharedPreferences.edit().putString("fullname", userFullname).apply()
            sharedPreferences.edit().putString("userId", userId).apply()
            sharedPreferences.edit().putString("id", id).apply()

            if (activateEditText!!){ replyEt.showKeyboard() }

            // this condition done to manipulate with UI
            if (userId == SessionManager(requireContext()).getUserId()) {
                followBtn.visibility = View.GONE
            } else {
                followBtn.visibility = View.VISIBLE
            }

            Glide.with(requireContext())
                .load(profileImage)
                .error(R.drawable.not_found)
                .centerCrop()
                .into(authorAvatar)

            authorFullname.text = userFullname ?: R.string.default_user_fullname.toString()
            authorUsername.text = "@$username"
            Markwon.create(requireContext()).setMarkdown(detailPostDescription, postDescription.toString())
            articlePageLikesCounter.text = likes

            replyEt.addAutoResizeTextWatcher(sentReply)

            whenCreated.text = createdAt?.formatCreatedAt()
            val dateConverter = convertDateFormat(updatedAt.toString())
            whenUpdated.text = dateConverter

            sentReply.isEnabled = false

            authorAvatar.setOnLongClickListener {
                detailUiViewModel.onAvatarLongPressed()
                true
            }

            followBtn.setOnClickListener { detailUiViewModel.onFollowPressed() }

            moreSettings.setOnClickListener { detailUiViewModel.onMorePressed() }

            sentReply.setOnClickListener { detailUiViewModel.onSendReplyPressed() }

            articlePageHeartIcon.setOnClickListener { detailUiViewModel.onLikePressed() }

            articlePageShareIcon.setOnClickListener { detailUiViewModel.onSharePressed() }

            detailLikesLayout.setOnClickListener { detailUiViewModel.onListOfLikesPressed() }

            detailPageCommentsIcon.setOnClickListener { detailUiViewModel.onCommentsPressed() }

            detailPageToolbar.setNavigationOnClickListener { detailUiViewModel.onBackPressed() }

            viewLifecycleOwner.lifecycleScope.launch {

                detailUiViewModel.detailPageEvent.collect{

                    when(it){
                        DetailPageUI.OnBackPressed -> { findNavController().navigateUp() }

                        DetailPageUI.OnCommentPressed -> { replyEt.showKeyboard() }

                        DetailPageUI.OnFollowPressed -> {

                            followingViewModel.followUsers(userId!!, "Bearer ${SessionManager(requireContext()).getToken()}")

                            followingViewModel.follow.observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is Follow.Success -> {
                                        detailRootLayout.snackbar(
                                            replyLayout,
                                            message = "now you follow: ${username?.uppercase()}",
                                        )
                                    }

                                    is Follow.Error -> {
                                        detailRootLayout.snackbarError(
                                            requireActivity().findViewById(R.id.reply_layout),
                                            error = result.message,
                                            ""
                                        ) {  }
                                    }
                                }
                            }
                        }

                        DetailPageUI.OnLikePressed -> { detailRootLayout.snackbar(requireActivity().findViewById(R.id.reply_layout), resources.getString(R.string.in_progress)) }

                        DetailPageUI.OnListOfLikesPressed -> { findNavController().navigate(R.id.action_detailFragment_to_listOfLikesFragment) }

                        DetailPageUI.OnMorePressed ->  { MoreSettingsDetailFragment().show(requireActivity().supportFragmentManager, R.string.detail_page_bottom_sheet.toString()) }

                        DetailPageUI.OnSendReplyPressed -> {

                            val reply = replyEt.text?.toString()?.trim()
                            tweetViewModel.postReply(reply!!, id!!, "Bearer ${SessionManager(requireContext()).getToken()}")
                            sentReply.isEnabled = false

                            tweetViewModel.postReplyResult.observe(viewLifecycleOwner) { result ->

                                when (result) {
                                    is PostReply.Success -> {
                                        replyEt.text?.clear()
                                        tweetViewModel.getRepliesOfPost(id)
                                        homeAdapter.notifyDataSetChanged()
                                        replyEt.addAutoResizeTextWatcher(sentReply)
                                    }

                                    is PostReply.Error -> {
                                        detailRootLayout.snackbarError(
                                            requireActivity().findViewById(R.id.reply_layout),
                                            result.message,
                                            ""){}
                                        replyEt.addAutoResizeTextWatcher(sentReply)
                                    }
                                }
                            }
                        }

                        DetailPageUI.OnSharePressed ->  { requireContext().shareUrl("https://twitturin.onrender.com/tweets/${id}") }

                        DetailPageUI.OnImagePressed -> { fullScreenImage(authorAvatar) }
                    }
                }
            }
        }
        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {

        binding.apply {
            val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
            val tweetId = sharedPreferences.getString("id", null)


            articleRcView.vertical().adapter = homeAdapter

            tweetViewModel.getRepliesOfPost(tweetId!!)

            repeatOnStarted {

                tweetViewModel.repliesOfPosts.collectLatest { response ->

                    if (response != null) {
                        if (response.isSuccessful) {

                            response.body()?.let { tweets ->
                                val tweetList: MutableList<Tweet> = tweets.toMutableList()
                                homeAdapter.differ.submitList(tweetList)

                                swipeToRefreshArticle.setOnRefreshListener {
                                    homeAdapter.notifyDataSetChanged()
                                    val freshList = tweetList.sortedByDescending { it.createdAt }
                                    tweetList.clear()
                                    tweetList.addAll(freshList)
                                    swipeToRefreshArticle.isRefreshing = false
                                }
                            }

                        } else {
                            detailRootLayout.snackbarError(
                                replyLayout,
                                response.body().toString(),
                                "") {  }
                        }
                    }
                }
            }
        }
    }

    private fun homeClickEvent(homeClickEvents: HomeAdapter.HomeClickEvents, tweet: Tweet) {

        when(homeClickEvents) {

            HomeAdapter.HomeClickEvents.ITEM -> {
                val bundle = Bundle().apply {
                    putString("userAvatar", tweet.author?.profilePicture)
                    putString("fullname", tweet.author?.fullName)
                    putString("username", tweet.author?.username)
                    putString("post_description", tweet.content)
                    putString("likes", tweet.likes.toString())
                    putString("createdAt", tweet.createdAt)
                    putString("updatedAt", tweet.updatedAt)
                    putString("userId", tweet.author?.id)
                    putString("id", tweet.id)
                }
                findNavController().navigate(R.id.detailFragment, bundle)
            }

            HomeAdapter.HomeClickEvents.HEART -> { Toast.makeText(requireContext(), "In progress", Toast.LENGTH_SHORT).show() }

            HomeAdapter.HomeClickEvents.SHARE -> { requireContext().shareUrl("https://twitturin.onrender.com/tweets/${tweet.id}") }

            HomeAdapter.HomeClickEvents.REPLY -> {
                val bundle = Bundle().apply {
                    putString("userAvatar", tweet.author?.profilePicture)
                    putString("fullname", tweet.author?.fullName)
                    putString("username", tweet.author?.username)
                    putString("post_description", tweet.content)
                    putString("likes", tweet.likes.toString())
                    putString("updatedAt", tweet.updatedAt)
                    putString("createdAt", tweet.createdAt)
                    putString("userId", tweet.author?.id)
                    putBoolean("activateEditText", true)
                    putString("id", tweet.id)
                }
                findNavController().navigate(R.id.detailFragment, bundle)
            }
        }
    }
}