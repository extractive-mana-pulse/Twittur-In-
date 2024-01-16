package com.example.twitturin.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentFollowingListBinding
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.FollowingAdapter
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.example.twitturin.viewmodel.manager.SessionManager
import com.google.android.material.snackbar.Snackbar
import java.util.Random

class FollowingListFragment : Fragment() {

    private lateinit var viewModel : MainViewModel
    private lateinit var binding : FragmentFollowingListBinding
    private val followingAdapter by lazy { FollowingAdapter(viewLifecycleOwner) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFollowingListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.anViewFollowing.setFailureListener { t ->
            snackbarError(t.message.toString())
        }
        binding.anViewFollowing.setAnimation(R.raw.empty_tweets_list)

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
                    binding.swipeToRefreshLayoutFollowingList.setOnRefreshListener {
                        tweetList.shuffle(Random(System.currentTimeMillis()))
                        viewModel.getFollowing(userId)
                        binding.swipeToRefreshLayoutFollowingList.isRefreshing = false
                    }

                    if (tweetList.isEmpty()){
                        binding.rcViewFollowing.visibility = View.GONE
                        binding.anViewFollowing.visibility = View.VISIBLE
                        binding.emptyFollowingTv.visibility = View.VISIBLE
                    } else {
                        binding.rcViewFollowing.visibility = View.VISIBLE
                        binding.anViewFollowing.visibility = View.GONE
                        binding.emptyFollowingTv.visibility = View.GONE

                        followingAdapter.setData(tweetList)
                    }
                }
            } else {
                snackbarError(response.body().toString())
            }
        }
    }

    private fun snackbarError(error : String) {
        val rootView = view?.findViewById<ConstraintLayout>(R.id.following_root_layout1)
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
        fun newInstance() = FollowingListFragment()
    }
}