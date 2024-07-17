package com.example.twitturin.search.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentSearchBinding
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.util.snackbarError
import com.example.twitturin.search.domain.model.SearchUser
import com.example.twitturin.search.presentation.adapters.SearchAdapter
import com.example.twitturin.search.presentation.sealed.SearchResource
import com.example.twitturin.search.presentation.vm.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val rqSpeechRec = 102
    private val searchViewModel : SearchViewModel by viewModels()
    private val followViewModel : FollowViewModel by viewModels()
    private val binding by lazy { FragmentSearchBinding.inflate(layoutInflater) }
    private val myAdapter by lazy { SearchAdapter(searchCLickEvents = ::searchClickEvent) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchFragment = this

        binding.apply {

            var job: Job? = null

            searchSearchView.editText.setOnEditorActionListener { v, actionId, event ->
                searchBar.text = searchSearchView.text
                searchSearchView.hide()
                job?.cancel()
                job = MainScope().launch {
                    delay(500)
                    searchSearchView.editText.let {
                        if (searchSearchView.text!!.isNotEmpty()) {
                            searchViewModel.searchString(searchBar.text.toString())
                        }
                    }
                }
                false
            }

            searchBar.inflateMenu(R.menu.searchbar_menu)
            binding.searchBar.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.mic -> { speech() }
                }
                true
            }

            searchRcView.layoutManager = LinearLayoutManager(requireContext())
            searchRcView.adapter = myAdapter

            searchViewModel.searchNews.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is SearchResource.Success -> {
                        hideProgressBar()
                        response.data?.let { newsResponse ->
                            myAdapter.differ.submitList(newsResponse.users)
                        }
                    }

                    is SearchResource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is SearchResource.Loading -> {
                        showProgressBar()
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.searchPaginationProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        binding.searchPaginationProgressBar.visibility = View.INVISIBLE
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rqSpeechRec && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.searchBar.text = result?.firstOrNull().toString()
        }
    }

    private fun speech(){
        if (!SpeechRecognizer.isRecognitionAvailable(requireActivity())){
            Toast.makeText(requireContext(), "Speech recognition is not available", Toast.LENGTH_SHORT).show()
        } else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak")
            startActivityForResult(i, rqSpeechRec)
        }
    }

    private fun searchClickEvent(searchCLickEvents: SearchAdapter.SearchCLickEvents, searchUser : SearchUser) {
        when(searchCLickEvents) {

            SearchAdapter.SearchCLickEvents.FOLLOW -> { followViewModel.followUsers(searchUser.id, "Bearer${SessionManager(requireContext()).getToken()}") }

            SearchAdapter.SearchCLickEvents.ITEM -> {
                val bundle = Bundle().apply {
                    putString("fullname", searchUser.fullName)
                    putString("username", searchUser.username)
                    putString("biography", searchUser.bio)
                    putBoolean("activateEditText", true)
                    putString("id", searchUser.id)
                }
                findNavController().navigate(R.id.observeProfileFragment, bundle)
            }
        }

        followViewModel.follow.observe(viewLifecycleOwner) {
            when(it) {
                is Follow.Success -> {
                    Snackbar.make(binding.searchRootLayout, searchUser.username, Snackbar.LENGTH_SHORT).show()
                }
                is Follow.Error -> {
                    binding.searchRootLayout.snackbarError(
                        binding.searchRootLayout,
                        error = it.message,
                        ""
                    ){}
                }
            }
        }
    }
}