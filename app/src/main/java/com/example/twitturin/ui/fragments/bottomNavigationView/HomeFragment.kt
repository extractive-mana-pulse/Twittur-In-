package com.example.twitturin.ui.fragments.bottomNavigationView

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.util.Random

class HomeFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding : FragmentHomeBinding
    private lateinit var toggle : ActionBarDrawerToggle
    private val postAdapter by lazy { PostAdapter(viewLifecycleOwner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeFragment = this

        val user = User()
        val url = user.profilePicture

        Glide.with(requireActivity())
            .load(url)
            .error(R.drawable.not_found)
            .centerCrop()
            .into(binding.accountImage)


        binding.accountImage.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        toggle = ActionBarDrawerToggle(requireActivity(), binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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

// TODO {work with this code and fix error getting null}

//        val headerView: View = binding.navigationView.getHeaderView(0)

//        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
//        val userImage = sharedPreferences.getString("userAvatar", "")
//        val fullname = sharedPreferences.getString("fullname", "")
//        val username = sharedPreferences.getString("username", "")
//
//        val imageView: ImageView = headerView.findViewById(R.id.nav_avatar)
//        val fullName: TextView = headerView.findViewById(R.id.nav_full_name_tv)
//        val userName: TextView = headerView.findViewById(R.id.nav_username_tv)
//
//        Glide.with(requireActivity())
//            .load("$userImage")
//            .into(imageView)
//
//        Log.d("fullname", fullname.toString())
//
//        fullName.text = fullname
//        userName.text = username

        updateRecyclerView()
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
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
                snackbarError()
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

    private fun snackbarError() {
        val error = "Something went wrong! Please refresh the page!"
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