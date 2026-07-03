package com.example.twitturin.detail.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.detail.presentation.screens.DetailScreen
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val tweetViewModel by viewModels<TweetViewModel>()
    private val followViewModel by viewModels<FollowViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tweet = (arguments?.getParcelable("tweet") as? Tweet) ?: Tweet()
        val token = "Bearer ${SessionManager(requireContext()).getToken()}"
        val isOwn = tweet.author?.id == SessionManager(requireContext()).getUserId()
        return ComposeView(requireContext()).apply {
            setContent {
                DetailScreen(
                    tweet = tweet,
                    viewModel = tweetViewModel,
                    isOwnTweet = isOwn,
                    onBack = { findNavController().navigateUp() },
                    onMore = {
                        findNavController().navigate(
                            DetailFragmentDirections.actionDetailFragmentToMoreSettingsDetailFragment(tweet)
                        )
                    },
                    onFollow = {
                        tweet.author?.id?.let { followViewModel.followUser(it, token) }
                        followViewModel.follow.observe(viewLifecycleOwner) { result ->
                            if (result is Follow.Success) {
                                Toast.makeText(
                                    requireContext(),
                                    "now you follow: ${tweet.author?.username}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    onListOfLikes = {
                        findNavController().navigate(
                            R.id.action_detailFragment_to_listOfLikesFragment,
                            Bundle().apply { putString("id", tweet.id) }
                        )
                    },
                    onSendReply = { text -> tweetViewModel.postReply(text, tweet.id.orEmpty(), token) },
                    onTweetClick = { t ->
                        findNavController().navigate(
                            R.id.detailFragment,
                            Bundle().apply {
                                putParcelable("tweet", t)
                                putBoolean("activateEditText", false)
                            }
                        )
                    },
                    onTweetShare = { t ->
                        requireContext().shareUrl("https://twitturin.onrender.com/tweets/${t.id}")
                    }
                )
            }
        }
    }
}
