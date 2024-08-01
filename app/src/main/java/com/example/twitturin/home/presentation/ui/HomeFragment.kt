package com.example.twitturin.home.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.appLanguage
import com.example.twitturin.core.extensions.appThemeDialog
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.core.extensions.loadToolbarImage
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.extensions.vertical
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentHomeBinding
import com.example.twitturin.home.presentation.adapter.HomeAdapter
import com.example.twitturin.home.presentation.sealed.HomeUIEvent
import com.example.twitturin.home.presentation.vm.HomeViewModel
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.shape.CornerFamily
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private val tweetViewModel by viewModels<TweetViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val homeAdapter by lazy { HomeAdapter(homeClickEvents = ::homeClickEvent) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    @SuppressLint("SetTextI18n", "UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeFragment = this

        binding.apply {

            val headerView: View = navigationView.getHeaderView(0)
            val headerViewAvatar: ImageView = headerView.findViewById(R.id.nav_avatar)
            val headerUsername: TextView = headerView.findViewById(R.id.nav_username_tv)
            val headerFullname: TextView = headerView.findViewById(R.id.nav_full_name_tv)
            val headerFollowing: TextView = headerView.findViewById(R.id.nav_following_counter_tv)
            val headingFollowers: TextView = headerView.findViewById(R.id.nav_followers_counter_tv)
            val layout: ShimmerFrameLayout = headerView.findViewById(R.id.navigation_drawer_shimmer)

            addPost.setOnClickListener { homeViewModel.onAddButtonPressed() }

            homePageToolbar.setNavigationOnClickListener { homeViewModel.onDrawerPressed() }

            repeatOnStarted {
                homeViewModel.homeEvent.collect { event ->
                    when(event){
                        is HomeUIEvent.OpenDrawer -> { drawerLayout.openDrawer(GravityCompat.START) }
                        is HomeUIEvent.NavigateToPublicPost -> { findNavController().navigate(R.id.action_homeFragment_to_publicPostFragment) }
                    }
                }
            }

            profileViewModel.getUserCredentials(SessionManager(requireContext()).getUserId()!!)
            repeatOnStarted {
                profileViewModel.getUserCredentials.collectLatest { result ->
                    layout.startShimmer()
                    when (result) {

                        is UserCredentials.Success -> {
                            layout.stopShimmer()
                            layout.beGone()

                            result.user.apply {
                                headerFullname.text = fullName ?: R.string.default_user_fullname.toString()
                                headerUsername.text = "@$username"
                                headerFollowing.text = followingCount.toString()
                                headingFollowers.text = followersCount.toString()

                                headerViewAvatar.loadImagesWithGlideExt(profilePicture)
                                homePageToolbar.loadToolbarImage(profilePicture, homePageToolbar)
                            }
                        }
                        is UserCredentials.Error -> { homeRootLayout.snackbarError(addPost, result.message, ""){} }
                        is UserCredentials.Loading -> {}
                    }
                }
            }

            navigationView.setNavigationItemSelectedListener { menuItem ->

                when(menuItem.itemId){
                    R.id.language_item -> requireActivity().appLanguage()
                    R.id.change_mode -> requireActivity().appThemeDialog()
                    R.id.time_table -> findNavController().navigate(R.id.action_homeFragment_to_webViewFragment)
                    R.id.profile_item -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    R.id.feedback_item -> findNavController().navigate(R.id.action_homeFragment_to_feedbackFragment)
                    R.id.settings_item -> findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
                }

                menuItem.isChecked = true
                drawerLayout.close()
                true
            }
            updateRecyclerView()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {

        binding.apply {

            tweetViewModel.getTweet(shimmerLayout)

            rcView.vertical().adapter = homeAdapter

            repeatOnStarted {
                tweetViewModel.responseOfTweet.collectLatest { response ->

                    if (response != null) {
                        if (response.isSuccessful) {

                            response.body()?.let { tweets ->

                                val tweetList: MutableList<Tweet> = tweets.toMutableList()
                                homeAdapter.differ.submitList(tweetList)

                                swipeToRefreshLayout.setOnRefreshListener {
                                    val freshList = tweetList.sortedByDescending { time -> time.createdAt }
                                    tweetList.clear()
                                    tweetList.addAll(freshList)
                                    homeAdapter.notifyDataSetChanged()
                                    swipeToRefreshLayout.isRefreshing = false
                                }
                            }

                        } else {
                            homeRootLayout.snackbarError(
                                requireActivity().findViewById(R.id.bottom_nav_view),
                                error = response.message().toString(),
                                ""){}
                        }
                    }
                }
            }
        }
    }

    private fun homeClickEvent(homeClickEvents: HomeAdapter.HomeClickEvents, tweet: Tweet) {

        val bottomNavView = requireActivity().findViewById<View>(R.id.bottom_nav_view)

        when(homeClickEvents) {

            HomeAdapter.HomeClickEvents.ITEM -> {
                val bundle = Bundle().apply {
                    putParcelable("tweet", tweet)
                    putBoolean("activateEditText", false)
                }
                findNavController().navigate(R.id.detailFragment, bundle)
            }

            HomeAdapter.HomeClickEvents.HEART -> { binding.homeRootLayout.snackbar(bottomNavView, resources.getString(R.string.developing)) }

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