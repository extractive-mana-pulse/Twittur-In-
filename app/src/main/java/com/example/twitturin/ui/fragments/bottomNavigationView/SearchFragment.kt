package com.example.twitturin.ui.fragments.bottomNavigationView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentSearchBinding
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.ui.sealeds.SearchResource
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

    private lateinit var binding : FragmentSearchBinding

    private lateinit var viewModel : MainViewModel

    private val myAdapter by lazy { PostAdapter(viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]


//        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(query: String?): Boolean {
//                var job : Job? = null
//
//                job?.cancel()
//                job = MainScope().launch {
//                    delay(500)
//                    query?.let {
//                        if (query.toString().isNotEmpty()){
//                            viewModel.searchString(Tweet)
//                        }
//                    }
//                }
//                if(query != null){
//                    searchDatabase()
//                }
//                return true
//            }
//        })

        binding.accountImageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_profileFragment)
        }
    }

//    private fun searchDatabase() {
//        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
//            when (response) {
//                is SearchResource.Success -> {
//                    hideProgressBar()
//                    response.data.let { newsResponse ->
//                        val tweetList: List<Tweet> = listOf(newsResponse)
//                        myAdapter.setData(tweetList)
//                    }
//                }
//
//                is SearchResource.Error -> {
//                    hideProgressBar()
//                }
//
//                is SearchResource.Loading -> {
//                    showProgressBar()
//                    Toast.makeText(requireContext(), "Loading . . .", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}