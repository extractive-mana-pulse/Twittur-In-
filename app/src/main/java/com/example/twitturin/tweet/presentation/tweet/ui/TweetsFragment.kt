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
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.core.extensions.beVisible
import com.example.twitturin.core.extensions.converter
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.extensions.vertical
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentTweetsBinding
import com.example.twitturin.detail.presentation.sealed.TweetDelete
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

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

                            rcView.beGone()
                            tweetsPageAnView.beVisible()
                            lottieInfoTv.beVisible()

                        } else {

                            rcView.beVisible()
                            tweetsPageAnView.beGone()
                            lottieInfoTv.beGone()
                            userPostAdapter.notifyDataSetChanged()
                            userPostAdapter.differ.submitList(tweetList)

                        }
                    }

                } else {
                    tweetsRootLayout.snackbarError(tweetsRootLayout, error = response.message(), ""){}
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
                            putParcelable("tweet", tweet)
                        }
                        findNavController().navigate(R.id.detailFragment, bundle)
                    }

                    TweetUIEvents.OnMorePressed -> {

                        val popupMenu = PopupMenu(context, moreSettingView)

                        popupMenu.setOnMenuItemClickListener { item ->
                            when (item.itemId) {

                                R.id.edit_user_own_tweet -> {
                                    val bundle = Bundle()
                                    bundle.putString("description", tweet.content)
                                    bundle.putString("tweetId", tweet.id)
                                    findNavController().navigate(R.id.editTweetFragment, bundle)
                                    true
                                }

                                R.id.delete_user_own_tweet -> {

                                    val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)

                                    alertDialogBuilder.apply {
                                        setTitle(requireContext().resources.getString(R.string.delete_tweet_title))
                                        setMessage(requireContext().resources.getString(R.string.delete_tweet_message))

                                        setPositiveButton(requireContext().resources.getString(R.string.yes)) { _, _ ->
                                            tweetViewModel.deleteTweet(tweet.id!!,"Bearer ${SessionManager(requireContext()).getToken()}")
                                        }

                                        setNegativeButton(requireContext().resources.getString(R.string.no)) { dialog, _ ->
                                            dialog.dismiss()
                                        }

                                        create().show()

                                        tweetViewModel.deleteTweetResult.observe(viewLifecycleOwner) { result ->

                                            when(result){
                                                is TweetDelete.Success -> {
                                                    create().dismiss()
                                                    Snackbar.make(binding.tweetsRootLayout, R.string.deleted, Snackbar.LENGTH_SHORT).show()
                                                }
                                                is  TweetDelete.Error -> {
                                                    create().dismiss()
                                                    Snackbar.make(binding.tweetsRootLayout, result.message, Snackbar.LENGTH_SHORT).show()
                                                }
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
                            putParcelable("tweet", tweet)
                            putBoolean("activateEditText", true)
                        }
                        findNavController().navigate(R.id.detailFragment, bundle)
                    }
                    TweetUIEvents.OnSharePressed -> { requireContext().shareUrl("https://twitturin.onrender.com/tweets/${tweet.id}") }
                }
            }
        }
    }
}