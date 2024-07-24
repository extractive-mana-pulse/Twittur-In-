package com.example.twitturin.detail.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.addAutoResizeTextWatcher
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.core.extensions.beVisible
import com.example.twitturin.core.extensions.convertDateFormat
import com.example.twitturin.core.extensions.formatCreatedAt
import com.example.twitturin.core.extensions.fullScreenImage
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.extensions.sharedPreferences
import com.example.twitturin.core.extensions.showKeyboard
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.extensions.stateDisabled
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

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val tweetViewModel by viewModels<TweetViewModel>()
    private val followingViewModel by viewModels<FollowViewModel>()
    private val detailUiViewModel by viewModels<DetailPageUIViewModel>()
    private val binding  by lazy { FragmentDetailBinding.inflate(layoutInflater) }
    private val homeAdapter by lazy { HomeAdapter(homeClickEvents = ::homeClickEvent) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailFragment = this

        var tweetId by requireActivity().sharedPreferences("tweetId")

        binding.apply {
            val activateEditText = arguments?.getBoolean("activateEditText", false)
            if (activateEditText!!){ replyEt.showKeyboard() }

            val data = arguments?.getParcelable("tweet") as? Tweet
            val id = data?.id
            if(id != null) { tweetId = id }

            data?.apply {
                authorUsername.text = "@${author?.username}"
                articlePageLikesCounter.text = likes.toString()
                whenCreated.text = createdAt?.formatCreatedAt()
                authorAvatar.loadImagesWithGlideExt(author?.profilePicture)
                authorFullname.text = author?.fullName ?: R.string.default_user_fullname.toString()
                Markwon.create(requireContext()).setMarkdown(detailPostDescription, content.toString())

                val dateConverter = convertDateFormat(updatedAt.toString())
                whenUpdated.text = dateConverter

                if (author?.id == SessionManager(requireContext()).getUserId()) { followBtn.beGone() } else { followBtn.beVisible() }
            }

//            val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
//            sharedPreferences.edit().putString("post_description", postDescription).apply()
//            sharedPreferences.edit().putString("userImage", profileImage).apply()
//            sharedPreferences.edit().putString("username", username).apply()
//            sharedPreferences.edit().putString("fullname", fullName).apply()
//            sharedPreferences.edit().putString("userId", id).apply()
//            sharedPreferences.edit().putString("id", id).apply()

            sentReply.stateDisabled()

            replyEt.addAutoResizeTextWatcher(sentReply)

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

            repeatOnStarted {

                detailUiViewModel.detailPageEvent.collect {

                    when(it){
                        DetailPageUI.OnBackPressed -> { findNavController().navigateUp() }

                        DetailPageUI.OnCommentPressed -> { replyEt.showKeyboard() }

                        DetailPageUI.OnFollowPressed -> {

                            followingViewModel.followUsers(data?.author?.id.toString(), "Bearer ${SessionManager(requireContext()).getToken()}")

                            followingViewModel.follow.observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is Follow.Success -> {
                                        detailRootLayout.snackbar(
                                            replyLayout,
                                            message = "now you follow: ${data?.author?.username?.uppercase()}",
                                        )
                                    }

                                    is Follow.Error -> {
                                        detailRootLayout.snackbarError(
                                            replyLayout,
                                            error = result.message,
                                            ""
                                        ) {  }
                                    }
                                }
                            }
                        }

                        DetailPageUI.OnLikePressed -> { detailRootLayout.snackbar(replyLayout, resources.getString(R.string.in_progress)) }

                        DetailPageUI.OnListOfLikesPressed -> { findNavController().navigate(R.id.action_detailFragment_to_listOfLikesFragment) }

                        DetailPageUI.OnMorePressed ->  { MoreSettingsDetailFragment().show(requireActivity().supportFragmentManager, R.string.detail_page_bottom_sheet.toString()) }

                        DetailPageUI.OnSendReplyPressed -> {

                            val reply = replyEt.text?.toString()?.trim()
                            tweetViewModel.postReply(reply!!, id.toString(), "Bearer ${SessionManager(requireContext()).getToken()}")
                            sentReply.isEnabled = false
                        }

                        DetailPageUI.OnSharePressed ->  { requireContext().shareUrl("https://twitturin.onrender.com/tweets/${id}") }

                        DetailPageUI.OnImagePressed -> { fullScreenImage(authorAvatar) }
                    }
                }

                repeatOnStarted {

                    tweetViewModel.postReplyResult.collectLatest { result ->
                        when (result) {
                            is PostReply.Success -> {
                                replyEt.text?.clear()
                                tweetViewModel.getRepliesOfPost(id.toString())
                                homeAdapter.notifyDataSetChanged()
                                replyEt.addAutoResizeTextWatcher(sentReply)
                            }

                            is PostReply.Error -> {
                                detailRootLayout.snackbarError(
                                    replyLayout,
                                    result.message,
                                    ""){}
                                replyEt.addAutoResizeTextWatcher(sentReply)
                            }
                            PostReply.Loading -> {  }
                        }
                    }
                }
            }
        }
        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {

        binding.apply {

            val tweetId by requireActivity().sharedPreferences("tweetId")

            articleRcView.vertical().adapter = homeAdapter

            tweetViewModel.getRepliesOfPost(tweetId)

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
                    putString("updatedAt", tweet.updatedAt)
                    putString("createdAt", tweet.createdAt)
                    putString("userId", tweet.author?.id)
                    putString("id", tweet.id)
                }
                findNavController().navigate(R.id.detailFragment, bundle)
            }

            HomeAdapter.HomeClickEvents.HEART -> { binding.replyLayout.snackbar(binding.replyLayout, R.string.in_progress.toString()) }

            HomeAdapter.HomeClickEvents.SHARE -> { requireContext().shareUrl("https://twitturin.onrender.com/tweets/${tweet.id}") }

            HomeAdapter.HomeClickEvents.REPLY -> {
                val bundle = Bundle().apply {
                    putParcelable("tweet", tweet)
                    putBoolean("activateEditText", true)
                }
                findNavController().navigate(R.id.detailFragment, bundle)
            }
        }
    }
}