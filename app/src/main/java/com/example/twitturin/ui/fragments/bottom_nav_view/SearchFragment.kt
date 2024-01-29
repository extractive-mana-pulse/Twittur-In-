package com.example.twitturin.ui.fragments.bottom_nav_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentSearchBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var viewModel : MainViewModel
    private lateinit var binding : FragmentSearchBinding
    @Inject lateinit var  snackbarHelper: SnackbarHelper
//    private val myAdapter by lazy { PostAdapter(viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        binding.techWorkAnView.setFailureListener { t ->
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
        }

        binding.techWorkAnView2.setFailureListener { t ->
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
        }

        binding.techWorkAnView3.setFailureListener { t ->
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}