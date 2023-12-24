package com.example.twitturin.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.databinding.FragmentFollowersListBinding
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.FollowersAdapter
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager

class FollowersListFragment : Fragment() {

    private lateinit var binding : FragmentFollowersListBinding
    private val followersAdapter by lazy { FollowersAdapter() }
    private lateinit var viewModel : MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFollowersListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]

        binding.backBtnFollowersList.setOnClickListener {
            requireActivity().onBackPressed()
        }

        updateRecyclerView()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.rcViewFollowers.adapter = followersAdapter
        binding.rcViewFollowers.addItemDecoration(DividerItemDecoration(binding.rcViewFollowers.context, DividerItemDecoration.VERTICAL))
        binding.rcViewFollowers.layoutManager = LinearLayoutManager(requireContext())
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        viewModel.getFollowers(userId!!)
        viewModel.followersList.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<User> = tweets.toMutableList()
                    followersAdapter.setData(tweetList)
                    followersAdapter.notifyDataSetChanged()
                }
            } else {
                Toast.makeText(requireContext(), response.code().toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FollowersListFragment()
    }
}