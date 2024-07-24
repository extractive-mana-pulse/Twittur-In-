package com.example.twitturin.tweet.presentation.tweet.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.extensions.vertical
import com.example.twitturin.databinding.FragmentTweetsBinding
import com.example.twitturin.detail.presentation.sealed.TweetDelete
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.core.extensions.converter
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.tweet.adapters.TweetAdapter
import com.example.twitturin.tweet.presentation.tweet.sealed.TweetUIEvents
import com.example.twitturin.tweet.presentation.tweet.vm.TweetUIViewModel
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Random

@AndroidEntryPoint
class TweetsFragment : Fragment() {

    private val tweetViewModel : TweetViewModel by viewModels()
    private val tweetUIViewModel : TweetUIViewModel by viewModels()
    private val binding by lazy { FragmentTweetsBinding.inflate(layoutInflater) }
    private val userPostAdapter by lazy { TweetAdapter(clickEvents = ::tweetClickEvents) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {

        binding.apply {

            rcView.vertical().adapter =userPostAdapter
            tweetViewModel.getUserTweet(SessionManager(requireContext()).getUserId()!!)

            tweetViewModel.userTweets.observe(requireActivity()) { response ->
                if (response.isSuccessful) {
                    response.body()?.let { tweets ->

                        val tweetList: MutableList<Tweet> = tweets.toMutableList()
                        swipeToRefreshLayoutTweets.setOnRefreshListener {

                            tweetViewModel.getUserTweet(SessionManager(requireContext()).getUserId()!!)
                            tweetList.shuffle(Random(System.currentTimeMillis()))
                            swipeToRefreshLayoutTweets.isRefreshing = false
                            userPostAdapter.notifyDataSetChanged()

                        }

                        if (tweetList.isEmpty()) {

                            rcView.visibility = View.GONE
                            tweetsPageAnView.visibility = View.VISIBLE
                            lottieInfoTv.visibility = View.VISIBLE

                        } else {

                            rcView.visibility = View.VISIBLE
                            tweetsPageAnView.visibility = View.GONE
                            lottieInfoTv.visibility = View.GONE
                            userPostAdapter.differ.submitList(tweetList)
                            userPostAdapter.notifyDataSetChanged()

                        }
                    }

                } else {
                    tweetsRootLayout.snackbarError(
                        requireActivity().findViewById(R.id.tweets_root_layout),
                        error = response.message(),
                        ""){}
                }
            }
        }
    }

    private fun tweetClickEvents(clickEvents: TweetAdapter.TweetClickEvents, tweet: Tweet) {
        when(clickEvents) {
            TweetAdapter.TweetClickEvents.ITEM -> { tweetUIViewModel.onItemPressed() }
            TweetAdapter.TweetClickEvents.REPLY -> { tweetUIViewModel.onReplyPressed() }
            TweetAdapter.TweetClickEvents.HEART -> { tweetUIViewModel.onHeartPressed() }
            TweetAdapter.TweetClickEvents.MORE -> { tweetUIViewModel.onMorePressed() }
            TweetAdapter.TweetClickEvents.SHARE -> { tweetUIViewModel.onSharePressed() }
        }

        val moreSettingView = requireActivity().findViewById<ImageButton>(R.id.more_settings_user_own_tweet)

        viewLifecycleOwner.lifecycleScope.launch {
            tweetUIViewModel.channel.collect {
                when(it){

                    TweetUIEvents.OnHeartPressed -> { Snackbar.make(binding.tweetsRootLayout, R.string.in_progress, Snackbar.LENGTH_SHORT).show() }

                    TweetUIEvents.OnItemPressed -> {
                        val bundle = Bundle().apply {
                        putString("id", tweet.id)
                        putString("userId", tweet.author?.id)
                        putString("createdAt", tweet.createdAt)
                        putString("updatedAt", tweet.updatedAt)
                        putString("likes", tweet.likes.toString())
                        putString("post_description", tweet.content)
                        putString("username", tweet.author?.username)
                        putString("fullname", tweet.author?.fullName)
                        putString("userAvatar", tweet.author?.profilePicture)
                        }
                        findNavController().navigate(R.id.detailFragment,bundle)
                    }

                    TweetUIEvents.OnMorePressed -> {

                        val popupMenu = PopupMenu(context, moreSettingView)

                        popupMenu.setOnMenuItemClickListener { item ->
                            when (item.itemId) {

                                R.id.edit_user_own_tweet -> {
                                    val bundle = Bundle()
                                    bundle.putString("description", tweet.content)
                                    findNavController().navigate(R.id.editTweetFragment, bundle)
                                    true
                                }

                                R.id.delete_user_own_tweet -> {

                                    val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)

                                    alertDialogBuilder.setTitle(requireContext().resources.getString(R.string.delete_tweet_title))
                                    alertDialogBuilder.setMessage(requireContext().resources.getString(R.string.delete_tweet_message))

                                    alertDialogBuilder.setPositiveButton(requireContext().resources.getString(R.string.yes)) { _, _ ->
                                        tweetViewModel.deleteTweet(tweet.id!!,"Bearer ${SessionManager(requireContext()).getToken()}")
                                    }

                                    alertDialogBuilder.setNegativeButton(requireContext().resources.getString(R.string.no)) { dialog, _ ->
                                        dialog.dismiss()
                                    }

                                    alertDialogBuilder.create().show()

                                    tweetViewModel.deleteTweetResult.observe(viewLifecycleOwner) { result ->

                                        when(result){
                                            is TweetDelete.Success -> {
                                                alertDialogBuilder.create().dismiss()
                                                Snackbar.make(binding.tweetsRootLayout, R.string.deleted, Snackbar.LENGTH_SHORT).show()
                                            }
                                            is  TweetDelete.Error -> {
                                                alertDialogBuilder.create().dismiss()
                                                Snackbar.make(binding.tweetsRootLayout, result.message, Snackbar.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    true
                                }
                                else -> false
                            }
                        }

                        popupMenu.inflate(R.menu.popup_user_menu)

                        popupMenu.converter(popupMenu)
                    }

                    TweetUIEvents.OnReplyPressed -> {
                        val bundle = Bundle().apply {
                            putString("id", tweet.id)
                            putString("userId", tweet.author?.id)
                            putString("updatedAt", tweet.updatedAt)
                            putString("createdAt", tweet.createdAt)
                            putString("likes", tweet.likes.toString())
                            putBoolean("activateEditText", true)
                            putString("post_description", tweet.content)
                            putString("username", tweet.author?.username)
                            putString("fullname", tweet.author?.fullName)
                            putString("userAvatar", tweet.author?.profilePicture)
                        }
                        findNavController().navigate(R.id.detailFragment, bundle)
                    }
                    TweetUIEvents.OnSharePressed -> { requireContext().shareUrl("https://twitturin.onrender.com/tweets/${tweet.id}") }
                }
            }
        }
    }
}