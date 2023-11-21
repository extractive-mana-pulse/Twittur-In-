package com.example.twitturin.presentation.fragments.bottomNavigationView

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentHomeBinding
import com.example.twitturin.presentation.adapters.PostAdapter
import com.example.twitturin.presentation.data.tweets.ApiTweetsItem
import com.example.twitturin.presentation.mvvm.MainViewModel
import com.example.twitturin.presentation.mvvm.Repository
import com.example.twitturin.presentation.mvvm.ViewModelFactory
import java.util.Random


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    private lateinit var viewModel: MainViewModel

    private val postAdapter by lazy { PostAdapter() }

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

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]


        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_publicPostFragment)
        }

        recyclerViewSetup()
        updateRecyclerView()
    }

    private fun recyclerViewSetup(){
//        this code is about divider in recyclerView programmatically
//        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
//        binding.rcView.addItemDecoration(dividerItemDecoration)
        binding.rcView.adapter = postAdapter
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
    }

    fun goToWebView(){
        findNavController().navigate(R.id.action_homeFragment_to_webViewFragment)
    }

    fun goToProfile(){
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
    }

//    fun goToPublicPost(){
//        findNavController().navigate(R.id.action_homeFragment_to_publicPostFragment)
//    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        viewModel.getTweet()
        viewModel.responseTweets.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<ApiTweetsItem> = tweets.toMutableList()
                    postAdapter.setData(tweetList)
                    binding.swipeToRefreshLayout.setOnRefreshListener {
                        binding.swipeToRefreshLayout.isRefreshing = false
                        tweetList.shuffle(Random(System.currentTimeMillis()))
                        postAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                Toast.makeText(requireContext(), response.code(), Toast.LENGTH_SHORT).show()
            }
        }
    }

//    fun testImage(){
//        findNavController().navigate(R.id.action_homeFragment_to_signInFragment)
//    }


    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}