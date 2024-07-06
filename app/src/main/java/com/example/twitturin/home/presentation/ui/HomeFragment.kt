package com.example.twitturin.home.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentHomeBinding
import com.example.twitturin.home.presentation.adapter.HomeAdapter
import com.example.twitturin.home.presentation.preferences.MyPreferences
import com.example.twitturin.home.presentation.sealed.HomeUIEvent
import com.example.twitturin.home.presentation.vm.HomeViewModel
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.util.snackbarError
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    private val homeViewModel : HomeViewModel by viewModels()
    private val tweetViewModel : TweetViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val homeAdapter by lazy { HomeAdapter(homeViewModel, viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    init {
        lifecycleScope.launchWhenStarted {
            try {
                tweetViewModel.getTweet(binding.shimmerLayout)
            } catch(e : Exception) {
                Snackbar.make(binding.homeRootLayout, e.message.toString(), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

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

            drawerIcon.setOnClickListener { homeViewModel.onDrawerPressed() }

            addPost.setOnClickListener { homeViewModel.onAddButtonPressed() }

            viewLifecycleOwner.lifecycleScope.launch {
                homeViewModel.homeEvent.collect {  event ->
                    when(event){
                        is HomeUIEvent.OpenDrawer -> { drawerLayout.openDrawer(GravityCompat.START) }
                        is HomeUIEvent.NavigateToPublicPost -> { findNavController().navigate(R.id.action_homeFragment_to_publicPostFragment) }
                    }
                }
            }

            profileViewModel.getUserCredentials(sessionManager.getUserId()!!)

            profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->

                layout.startShimmer()
                when (result) {

                    is UserCredentials.Success -> {

                        layout.stopShimmer()
                        layout.visibility = View.GONE

                        // navigation drawer header layout imageView
                        Glide.with(requireContext())
                            .load(result.user.profilePicture)
                            .error(R.drawable.not_found)
                            .placeholder(R.drawable.person)
                            .into(headerViewAvatar)

                        // top bar navigation icon
                        Glide.with(requireContext())
                            .load(result.user.profilePicture)
                            .error(R.drawable.not_found)
                            .placeholder(R.drawable.person)
                            .into(drawerIcon)

                        result.user.apply {
                            headerFullname.text = fullName ?: "Twittur User"
                            headerUsername.text = "@$username"
                            headerFollowing.text = followingCount.toString()
                            headingFollowers.text = followersCount.toString()
                        }
                    }

                    is UserCredentials.Error -> {
                        homeRootLayout.snackbarError(
                            view.findViewById<TextView>(R.id.tv_for_snackbar),
                            error = result.message,
                            ""){}
                    }
                }
            }

            navigationView.setNavigationItemSelectedListener { menuItem ->
                when(menuItem.itemId){
                    R.id.language_item -> appLanguage()
                    R.id.change_mode -> appThemeDialog()
                    R.id.time_table -> findNavController().navigate(R.id.action_homeFragment_to_webViewFragment)
                    R.id.profile_item -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    R.id.feedback_item -> {
                        findNavController().navigate(R.id.action_homeFragment_to_feedbackFragment)
                        drawerLayout.close()
                    }
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

            rcView.adapter = homeAdapter
            rcView.layoutManager = LinearLayoutManager(requireContext())
            rcView.addItemDecoration(DividerItemDecoration(rcView.context, DividerItemDecoration.VERTICAL))

            tweetViewModel.responseTweets.observe(requireActivity()) { response ->

                if (response.isSuccessful) {

                    response.body()?.let { tweets ->

                        val tweetList: MutableList<Tweet> = tweets.toMutableList()
                        homeAdapter.differ.submitList(tweetList)

                        swipeToRefreshLayout.setOnRefreshListener {
                            val freshList = tweetList.sortedByDescending { time -> time.createdAt }
                            tweetList.clear()
                            tweetList.addAll(freshList)
                            tweetViewModel.getTweet(shimmerLayout)
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

    private fun appThemeDialog() {

        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        builder.setTitle(resources.getString(R.string.change_theme))
        val styles = arrayOf("Light", "Dark", "System default")
        val checkedItem = MyPreferences(requireContext()).darkMode

        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->

            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    MyPreferences(requireContext()).darkMode = 0
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    MyPreferences(requireContext()).darkMode = 1
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    MyPreferences(requireContext()).darkMode = 2
                    dialog.dismiss()
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun appLanguage() {
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        builder.setTitle(resources.getString(R.string.choose_language))
        val styles = arrayOf("en", "it", "ru", "uz")
        builder.setSingleChoiceItems(styles, -1) { dialog, which ->
            when (which) {
                0 -> {
                    setLocale("en")
                    requireActivity().recreate()
                }
                1 -> {
                    setLocale("it")
                    requireActivity().recreate()
                }
                2 -> {
                    setLocale("ru")
                    requireActivity().recreate()
                }
                3 -> {
                    setLocale("uz")
                    requireActivity().recreate()
                }
            }
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("CommitPrefEdits")
    private fun setLocale(lang : String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)

        val editor = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("lang", lang)
        editor.apply()
    }
}