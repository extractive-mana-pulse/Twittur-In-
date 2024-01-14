package com.example.twitturin.ui.fragments.bottomNavigationView

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
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
import com.example.twitturin.ui.fragments.bottomsheets.LanguageFragment
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
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
                R.id.language_item -> LanguageFragment().show(requireActivity().supportFragmentManager, "LanguageFragment")
                R.id.time_table -> findNavController().navigate(R.id.action_homeFragment_to_webViewFragment)
            }
            menuItem.isChecked = true
            binding.drawerLayout.close()
            true
        }

// TODO {work with this code and fix error getting null}

//        val headerView: View = binding.navigationView.getHeaderView()
//
//        val imageView: ImageView = headerView.findViewById(R.id.nav_avatar)
//        val textView: TextView = headerView.findViewById(R.id.nav_full_name_tv)
//
//        Glide.with(requireActivity())
//            .load(url)
//            .into(imageView)
//
//        textView.text = user.fullName

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
                Toast.makeText(requireContext(), response.code().toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun goToWebView(){
        findNavController().navigate(R.id.action_homeFragment_to_webViewFragment)
    }

    fun goToProfile(){
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
    }

    fun goToPublicPost(){
        findNavController().navigate(R.id.action_homeFragment_to_publicPostFragment)
    }


    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}