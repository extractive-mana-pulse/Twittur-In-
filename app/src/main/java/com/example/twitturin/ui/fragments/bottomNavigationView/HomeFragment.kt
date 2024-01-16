package com.example.twitturin.ui.fragments.bottomNavigationView

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentHomeBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ProfileViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager
import com.google.android.material.snackbar.Snackbar
import java.util.Random

class HomeFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding : FragmentHomeBinding
    private val postAdapter by lazy { PostAdapter(viewLifecycleOwner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeFragment = this

        val sharedPreferences = requireContext().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val userImage = sharedPreferences.getString("userImage", "")
        val fullname = sharedPreferences.getString("fullname", "")
        val username = sharedPreferences.getString("username", "")

        // TODO {work with this code and fix error getting null}

        val headerView: View = binding.navigationView.getHeaderView(0)

        headerView.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) }

        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        profileViewModel.getUserCredentials(userId!!)

        profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->

            when (result) {
                is UserCredentialsResult.Success -> {

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

                    fullName.text = result.user.fullName ?: "Twittur User"

                    userName.text = "@" + result.user.username

                    followingTv.text = result.user.followingCount.toString()
                    followersTv.text = result.user.followersCount.toString()

                }
                is UserCredentialsResult.Error -> {
                    snackbarError(result.message)
                }
            }
        }





//
//        Glide.with(requireActivity())
//            .load(userImage)
//            .into(imageView)
//
//        fullName.text = fullname
//        userName.text = username

        Glide.with(requireActivity())
            .load(userImage)
            .error(R.drawable.not_found)
            .centerCrop()
            .into(binding.accountImage)


        binding.accountImage.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        binding.navigationView.setNavigationItemSelectedListener {menuItem ->
            when(menuItem.itemId){
                R.id.profile_item -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                R.id.language_item ->  snackbar() /*LanguageFragment().show(requireActivity().supportFragmentManager, "LanguageFragment")*/
                R.id.time_table -> findNavController().navigate(R.id.action_homeFragment_to_webViewFragment)
            }
            menuItem.isChecked = true
            binding.drawerLayout.close()
            true
        }
        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.rcView.adapter = postAdapter
        binding.rcView.addItemDecoration(DividerItemDecoration(binding.rcView.context, DividerItemDecoration.VERTICAL))
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getTweet()
        viewModel.responseTweets.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<Tweet> = tweets.toMutableList()
                    postAdapter.setData(tweetList)
                    binding.swipeToRefreshLayout.setOnRefreshListener {
                        tweetList.shuffle(Random(System.currentTimeMillis()))
                        postAdapter.notifyDataSetChanged()
                        binding.swipeToRefreshLayout.isRefreshing = false
                    }
                }
            } else {
                snackbarError(error = "Something went wrong, Please refresh page!")
            }
        }
    }

    fun goToPublicPost(){
        findNavController().navigate(R.id.action_homeFragment_to_publicPostFragment)
    }

    private fun snackbar() {
        val error = "In Progress"
        val rootView = view?.findViewById<DrawerLayout>(R.id.drawer_layout)
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = Snackbar
            .make(rootView!!, error, duration)
            .setBackgroundTint(resources.getColor(R.color.md_theme_light_primary))
            .setTextColor(resources.getColor(R.color.md_theme_light_onPrimaryContainer))
        snackbar.show()
    }

    private fun snackbarError(error : String) {
        val rootView = view?.findViewById<DrawerLayout>(R.id.drawer_layout)
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = Snackbar
            .make(rootView!!, error, duration)
            .setBackgroundTint(resources.getColor(R.color.md_theme_light_errorContainer))
            .setTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
            .setActionTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
        snackbar.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}