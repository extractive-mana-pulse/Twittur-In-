package com.example.twitturin.ui.fragments.bottom_nav_view

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.twitturin.MyPreferences
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentHomeBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.tweet.model.data.Tweet
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.profile.sealed.UserCredentials
import com.example.twitturin.viewmodel.LikeViewModel
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.profile.vm.ProfileViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    @Inject lateinit var snackbarHelper: SnackbarHelper
    @Inject lateinit var sessionManager: SessionManager
    private val lViewModel: LikeViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val postAdapter by lazy { PostAdapter(lViewModel, viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    @SuppressLint("SetTextI18n", "UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeFragment = this

        /* this block of code for testing purpose only */

        binding.apply {
//            testMode.setOnClickListener { appLanguage() }
        }

//        binding.testImage.setOnClickListener {
//            activity?.let {
//                val intent = Intent(it, PhotoPickerActivity::class.java)
//                startActivity(intent)
//            }
//        }

        /* this block of code for testing purpose only */

        val headerView: View = binding.navigationView.getHeaderView(0)

//        headerView.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) }

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

                is UserCredentials.Error -> {

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
        val styles = arrayOf("en", "it", "ru","uz")
        builder.setSingleChoiceItems(styles, -1) { dialog, which ->
            if (which==0) {
                setLocale("en")
                requireActivity().recreate()
            } else if (which==1) {
                setLocale("it")
                requireActivity().recreate()
            } else if (which==2) {
                setLocale("ru")
                requireActivity().recreate()
            } else if (which==3) {
                setLocale("uz")
                requireActivity().recreate()
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

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}