package com.example.twitturin.ui.decoration

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentFollowersListBinding
import com.example.twitturin.databinding.FragmentFollowingListBinding
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.FollowersAdapter
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager

class FollowingListFragment : Fragment() {

    private lateinit var binding : FragmentFollowingListBinding
    private val followingAdapter by lazy { FollowersAdapter() }
    private lateinit var viewModel : MainViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFollowingListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]

        binding.backBtnFollowingList.setOnClickListener {
            requireActivity().onBackPressed()
        }
        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.rcViewFollowing.adapter = followingAdapter
        binding.rcViewFollowing.addItemDecoration(DividerItemDecoration(binding.rcViewFollowing.context, DividerItemDecoration.VERTICAL))
        binding.rcViewFollowing.layoutManager = LinearLayoutManager(requireContext())
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        viewModel.getFollowing(userId!!)
        viewModel.followingList.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->
                    val tweetList: MutableList<User> = tweets.toMutableList()
                    followingAdapter.setData(tweetList)
                    followingAdapter.notifyDataSetChanged()
                }
            } else {
                Toast.makeText(requireContext(), response.code().toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FollowingListFragment()
    }
}