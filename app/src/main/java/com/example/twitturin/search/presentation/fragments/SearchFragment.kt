package com.example.twitturin.search.presentation.fragments

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
import com.example.twitturin.databinding.FragmentSearchBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.search.presentation.adapters.SearchAdapter
import com.example.twitturin.search.presentation.vm.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val rqSpeechRec = 102
    private val myAdapter by lazy { SearchAdapter() }
    @Inject lateinit var  snackbarHelper: SnackbarHelper
    private val searchViewModel : SearchViewModel by viewModels()
    private val binding by lazy { FragmentSearchBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchFragment = this

        binding.apply {

//            var job: Job? = null
//
//            binding.searchView.editText.setOnEditorActionListener { v, actionId, event ->
//                binding.searchBar.text = binding.searchView.text
//                binding.searchView.hide()
//                job?.cancel()
//                job = MainScope().launch {
//                    delay(500)
//                    binding.searchView.editText.let {
//                        if (binding.searchView.text!!.isNotEmpty()) {
//                            searchViewModel.searchString(binding.searchBar.text.toString())
//                        }
//                    }
//                }
//                false
//            }
//
//            binding.searchBar.inflateMenu(R.menu.searchbar_menu)
//            binding.searchBar.setOnMenuItemClickListener { menuItem ->
//                when(menuItem.itemId) {
//                    R.id.mic -> { speech() }
//                }
//                true
//            }
//
//            binding.rcView.layoutManager = LinearLayoutManager(requireContext())
//            binding.rcView.adapter = myAdapter
//
//            searchViewModel.searchNews.observe(viewLifecycleOwner) { response ->
//                when (response) {
//                    is SearchResource.Success -> {
//                        hideProgressBar()
//                        response.data?.let { newsResponse ->
//                            myAdapter.setData(newsResponse.response.user)
//                        }
//                    }
//                    is SearchResource.Error -> {
//                        hideProgressBar()
//                        response.message?.let { message ->
//                            Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    is SearchResource.Loading -> {
//                        showProgressBar()
//                    }
//                }
//            }
        }
    }

//    private fun showProgressBar() {
//        binding.paginationProgressBar.visibility = View.VISIBLE
//    }
//
//    private fun hideProgressBar(){
//        binding.paginationProgressBar.visibility = View.INVISIBLE
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == rqSpeechRec && resultCode == Activity.RESULT_OK) {
//            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//            binding.searchBar.text = result?.firstOrNull().toString()
//        }
//    }

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
}