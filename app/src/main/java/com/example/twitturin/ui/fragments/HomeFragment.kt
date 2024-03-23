package com.example.twitturin.ui.fragments

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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentHomeBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.preferences.MyPreferences
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.example.twitturin.tweet.presentation.model.data.Tweet
import com.example.twitturin.tweet.presentation.vm.LikeViewModel
import com.example.twitturin.tweet.presentation.vm.TweetViewModel
import com.example.twitturin.ui.adapters.PostAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject lateinit var snackbarHelper: SnackbarHelper
    @Inject lateinit var sessionManager: SessionManager
    private val likeViewModel: LikeViewModel by viewModels()
    private val tweetViewModel : TweetViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val postAdapter by lazy { PostAdapter(likeViewModel, viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    /**этот код пишется тестовом режиме надо будет потом испробовать все нюансы!*/
    init {
        lifecycleScope.launchWhenStarted {
            try {
                tweetViewModel.getTweet(binding.shimmerLayout)
            } finally {
                // This line might execute after Lifecycle is DESTROYED.
                if (lifecycle.currentState >= Lifecycle.State.STARTED) {
                    // Here, since we've checked, it is safe to run any
                    // Fragment transactions.
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeFragment = this
//        checkConnection()
        val headerView: View = binding.navigationView.getHeaderView(0)

        val userId = sessionManager.getUserId()

        val layout: ShimmerFrameLayout = headerView.findViewById(R.id.navigation_drawer_shimmer)

        profileViewModel.getUserCredentials(userId!!)

        profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->

            layout.startShimmer()

            when (result) {

                is UserCredentials.Success -> {

                    layout.stopShimmer()
                    layout.visibility = View.GONE

                    val imageView: ImageView = headerView.findViewById(R.id.nav_avatar)
                    val fullName: TextView = headerView.findViewById(R.id.nav_full_name_tv)
                    val userName: TextView = headerView.findViewById(R.id.nav_username_tv)
                    val followingTv: TextView = headerView.findViewById(R.id.nav_following_counter_tv)
                    val followersTv: TextView = headerView.findViewById(R.id.nav_followers_counter_tv)

                    val profileImage = "${result.user.profilePicture}"

                    Glide.with(requireContext())
                        .load(profileImage)
                        .error(R.drawable.not_found)
                        .placeholder(R.drawable.person)
                        .into(imageView)

                    Glide.with(requireContext())
                        .load(profileImage)
                        .error(R.drawable.not_found)
                        .placeholder(R.drawable.person)
                        .into(binding.accountImage)

                    fullName.text = result.user.fullName ?: "Twittur User"
                    userName.text = "@" + result.user.username
                    followingTv.text = result.user.followingCount.toString()
                    followersTv.text = result.user.followersCount.toString()

                }

                is UserCredentials.Error -> {

                    snackbarHelper.snackbarError(
                        view.findViewById<DrawerLayout>(R.id.drawer_layout),
                        view.findViewById<TextView>(R.id.tv_for_snackbar),
                        result.message,
                        ""
                    ){}
                }
            }
        }

        binding.accountImage.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navigationView.setNavigationItemSelectedListener {menuItem ->
            when(menuItem.itemId){
                R.id.profile_item -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                R.id.language_item ->  appLanguage()
                R.id.time_table -> findNavController().navigate(R.id.action_homeFragment_to_webViewFragment)
                R.id.change_mode -> appThemeDialog()
            }
            menuItem.isChecked = true
            binding.drawerLayout.close()
            true
        }
        updateRecyclerView()
    }


    /** need to optimize code */
    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {

        binding.apply {

            rcView.adapter = postAdapter
            rcView.layoutManager = LinearLayoutManager(requireContext())
            rcView.addItemDecoration(DividerItemDecoration(rcView.context, DividerItemDecoration.VERTICAL))

//            tweetViewModel.getTweet(shimmerLayout)

            tweetViewModel.responseTweets.observe(requireActivity()) { response ->
                if (response.isSuccessful) {
                    response.body()?.let { tweets ->
                        val tweetList: MutableList<Tweet> = tweets.toMutableList()
                        postAdapter.setData(tweetList)
                        swipeToRefreshLayout.setOnRefreshListener {
                            val freshList = tweetList.sortedByDescending { time -> time.createdAt }
                            tweetList.clear()
                            tweetList.addAll(freshList)
//                            tweetViewModel.getTweet(shimmerLayout)
                            postAdapter.notifyDataSetChanged()
                            swipeToRefreshLayout.isRefreshing = false
                        }
                    }
                } else {
                    snackbarHelper.snackbarError(
                        requireActivity().findViewById(R.id.drawer_layout),
                        requireActivity().findViewById(R.id.bottom_nav_view),
                        response.message().toString(),
                        ""){}
                }
            }
        }
    }

    /**this method use data binding to handle navigation !*/

    fun goToPublicPost(){
        findNavController().navigate(R.id.action_homeFragment_to_publicPostFragment)
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

//    private fun checkConnection() {
//
//        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkInfo = connectivityManager.activeNetworkInfo
//
//        if (networkInfo != null && networkInfo.isConnected) {
//            findNavController().navigate(R.id.homeFragment)
//        } else {
//            findNavController().navigate(R.id.noInternetFragment)
//        }
//    }
}