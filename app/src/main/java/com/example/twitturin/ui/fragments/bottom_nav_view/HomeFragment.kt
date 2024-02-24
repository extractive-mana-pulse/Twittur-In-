package com.example.twitturin.ui.fragments.bottom_nav_view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentHomeBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.activities.PhotoPickerActivity
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.viewmodel.LikeViewModel
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ProfileViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.manager.SessionManager
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding : FragmentHomeBinding
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper
    private val profileViewModel: ProfileViewModel by viewModels()
    private val lViewModel: LikeViewModel by viewModels()
    private val postAdapter by lazy { PostAdapter(lViewModel, viewLifecycleOwner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeFragment = this

        /* this block of code for testing purpose only */

        binding.testImage.setOnClickListener {
            activity?.let {
                val intent = Intent(it, PhotoPickerActivity::class.java)
                startActivity(intent)
            }
        }

        /* this block of code for testing purpose only */

        val headerView: View = binding.navigationView.getHeaderView(0)
        val themeButton : ImageButton = headerView.findViewById(R.id.light_mode_dark_mode)

        var isDarkTheme = false

        themeButton.setOnClickListener {
            isDarkTheme = !isDarkTheme

            if (isDarkTheme) {
//                activity?.setTheme(R.style.AppTheme_Dark)
                AppCompatDelegate.MODE_NIGHT_YES
                themeButton.setImageResource(R.drawable.dark_mode)
            } else {
//                activity?.setTheme(R.style.AppTheme)
                AppCompatDelegate.MODE_NIGHT_NO
                themeButton.setImageResource(R.drawable.light_mode)
            }
            activity?.recreate()
        }

        headerView.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) }

        val userId = sessionManager.getUserId()

        val layout: ShimmerFrameLayout = headerView.findViewById(R.id.navigation_drawer_shimmer)

        profileViewModel.getUserCredentials(userId!!)

        profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->

            layout.startShimmer()

            when (result) {

                is UserCredentialsResult.Success -> {

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
                        .placeholder(R.drawable.username_person)
                        .into(imageView)

                    Glide.with(requireContext())
                        .load(profileImage)
                        .error(R.drawable.not_found)
                        .placeholder(R.drawable.username_person)
                        .into(binding.accountImage)

                    fullName.text = result.user.fullName ?: "Twittur User"
                    userName.text = "@" + result.user.username
                    followingTv.text = result.user.followingCount.toString()
                    followersTv.text = result.user.followersCount.toString()

                }

                is UserCredentialsResult.Error -> {

                    snackbarHelper.snackbarError(
                        view.findViewById<DrawerLayout>(R.id.drawer_layout),
                        view.findViewById(R.id.bottom_nav_view),
                        result.message,
                        ""
                    ){}
                }
            }
        }

        binding.accountImage.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        binding.navigationView.setNavigationItemSelectedListener {menuItem ->
            when(menuItem.itemId){
                R.id.profile_item -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                R.id.language_item ->  snackbarHelper.snackbar(
                    requireActivity().findViewById(R.id.drawer_layout),
                    requireActivity().findViewById(R.id.add_post),
                    message = resources.getString(R.string.in_progress)
                ) /*LanguageFragment().show(requireActivity().supportFragmentManager, "LanguageFragment")*/
                R.id.time_table -> findNavController().navigate(R.id.action_homeFragment_to_webViewFragment)
            }
            menuItem.isChecked = true
            binding.drawerLayout.close()
            true
        }

        updateRecyclerView()
    }

    /** This method implement advanced recyclerview setting like a divider and swipe to refresh layout ! */

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.rcView.adapter = postAdapter
        binding.rcView.addItemDecoration(DividerItemDecoration(binding.rcView.context, DividerItemDecoration.VERTICAL))
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getTweet(binding.shimmerLayout)
        viewModel.responseTweets.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<Tweet> = tweets.toMutableList()
                    postAdapter.setData(tweetList)
                    binding.swipeToRefreshLayout.setOnRefreshListener {
                        val freshList = tweetList.sortedByDescending { it.createdAt }
                        tweetList.clear()
                        tweetList.addAll(freshList)
                        viewModel.getTweet(binding.shimmerLayout)
                        postAdapter.notifyDataSetChanged()
                        binding.swipeToRefreshLayout.isRefreshing = false
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

    /**this method use data binding to handle navigation !*/

    fun goToPublicPost(){
        findNavController().navigate(R.id.action_homeFragment_to_publicPostFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}