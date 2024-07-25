package com.example.twitturin.detail.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.core.extensions.vertical
import com.example.twitturin.databinding.FragmentListOfLikesBinding
import com.example.twitturin.detail.domain.model.UserLikesAPost
import com.example.twitturin.detail.presentation.adapter.ListOfLikesAdapter
import com.example.twitturin.detail.presentation.vm.ListOfLikesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListOfLikesFragment : Fragment() {

    private val listOfLikesAdapter by lazy { ListOfLikesAdapter() }
    private val listOfLikesViewModel : ListOfLikesViewModel by viewModels()
    private val binding by lazy { FragmentListOfLikesBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listOfLikesToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {

        binding.apply {
            val tweetId = arguments?.getString("id", "")

            listOfLikesRcView.vertical().adapter = listOfLikesAdapter

            listOfLikesViewModel.getListOfUsersLikesAPost(tweetId!!)

            listOfLikesViewModel.listOfLikesToThePosts.observe(viewLifecycleOwner) { response ->
                if (response.isSuccessful) {
                    response.body()?.let { tweets ->
                        val tweetList: MutableList<UserLikesAPost> = tweets.toMutableList()
                        listOfLikesAdapter.differ.submitList(tweetList)
                        swipeListOfLikesLayout.setOnRefreshListener {
                            listOfLikesAdapter.notifyDataSetChanged()
                            swipeListOfLikesLayout.isRefreshing = false
                        }
                    }
                } else {
                    Snackbar.make(specialUiForSnackbarView, response.body().toString(), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}