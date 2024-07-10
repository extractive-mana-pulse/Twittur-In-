package com.example.twitturin.detail.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentDetailBinding
import com.example.twitturin.detail.presentation.sealed.DetailPageUI
import com.example.twitturin.detail.presentation.sealed.PostReply
import com.example.twitturin.detail.presentation.util.addAutoResizeTextWatcher
import com.example.twitturin.detail.presentation.util.formatCreatedAt
import com.example.twitturin.detail.presentation.util.showKeyboard
import com.example.twitturin.detail.presentation.vm.DetailPageUIViewModel
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.home.presentation.adapter.HomeAdapter
import com.example.twitturin.home.presentation.vm.HomeViewModel
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.fragments.FullScreenImageFragment
import com.example.twitturin.profile.presentation.util.snackbar
import com.example.twitturin.profile.presentation.util.snackbarError
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailFragment : Fragment() {

    @Inject lateinit var sessionManager : SessionManager
    private val homeViewModel : HomeViewModel by viewModels()
    private val tweetViewModel : TweetViewModel by viewModels()
    private val followingViewModel : FollowViewModel by viewModels()
    private val detailUiViewModel : DetailPageUIViewModel by viewModels()
    private val binding  by lazy { FragmentDetailBinding.inflate(layoutInflater) }
    private val homeAdapter by lazy { HomeAdapter(homeViewModel, viewLifecycleOwner) }

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
            if (userId == sessionManager.getUserId()) {
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

                            followingViewModel.followUsers(userId!!, "Bearer ${sessionManager.getToken()}")

                            followingViewModel.follow.observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is Follow.Success -> {
                                        detailRootLayout.snackbar(
                                            requireActivity().findViewById(R.id.reply_layout),
                                            message = "now you follow: ${username?.uppercase()}",
                                        )
                                    }

                                    is Follow.Error -> {
                                        detailRootLayout.snackbarError(
                                            requireActivity().findViewById(R.id.reply_layout),
                                            error = result.message,
                                            ""
                                        ) { /* actionCallBack ->  Unit */ }
                                    }
                                }
                            }
                        }

                        DetailPageUI.OnLikePressed -> { detailRootLayout.snackbar(requireActivity().findViewById(R.id.reply_layout), resources.getString(R.string.in_progress)) }

                        DetailPageUI.OnListOfLikesPressed -> { findNavController().navigate(R.id.action_detailFragment_to_listOfLikesFragment) }

                        DetailPageUI.OnMorePressed ->  { MoreSettingsDetailFragment().show(requireActivity().supportFragmentManager, R.string.detail_page_bottom_sheet.toString()) }

                        DetailPageUI.OnSendReplyPressed -> {

                            val reply = replyEt.text?.toString()?.trim()
                            tweetViewModel.postReply(reply!!, id!!, "Bearer ${sessionManager.getToken()}")
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

                        DetailPageUI.OnSharePressed ->  { shareData() }

                        DetailPageUI.OnImagePressed -> {

                            val fullScreenImageFragment = FullScreenImageFragment()

                            authorAvatar.buildDrawingCache()
                            val originalBitmap = authorAvatar.drawingCache
                            val image = originalBitmap.copy(originalBitmap.config, true)

                            val extras = Bundle()
                            Bundle().putParcelable("image", image)
                            fullScreenImageFragment.arguments = extras

                            fullScreenImageFragment.show(requireActivity().supportFragmentManager, "FullScreenImageFragment")
                        }
                    }
                }
            }
        }
        updateRecyclerView()
    }

    private fun shareData() {

        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id", null)

        val intent = Intent(Intent.ACTION_SEND).apply {
            val baseUrl = "https://twitturin.onrender.com/tweets/$id"

            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, baseUrl)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, "Choose app:"))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {

        binding.apply {
            val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
            val tweetId = sharedPreferences.getString("id", null)

            articleRcView.adapter = homeAdapter
            articleRcView.layoutManager = LinearLayoutManager(requireContext())
            articleRcView.addItemDecoration(DividerItemDecoration(articleRcView.context, DividerItemDecoration.VERTICAL))

            tweetViewModel.getRepliesOfPost(tweetId!!)

            tweetViewModel.repliesOfPosts.observe(viewLifecycleOwner) { response ->
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
                        requireActivity().findViewById(R.id.reply_layout),
                        response.body().toString(),
                        "") {  }
                }
            }
        }
    }

    private fun convertDateFormat(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss", Locale.getDefault())

        val date = inputFormat.parse(dateString)
        return outputFormat.format(date!!)
    }
}