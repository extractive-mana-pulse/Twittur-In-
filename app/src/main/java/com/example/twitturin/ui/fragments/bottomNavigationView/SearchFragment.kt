package com.example.twitturin.ui.fragments.bottomNavigationView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentSearchBinding
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.adapters.PostAdapter
import com.example.twitturin.viewmodel.MainViewModel
import com.example.twitturin.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class SearchFragment : Fragment() {

    private lateinit var viewModel : MainViewModel
    private lateinit var binding : FragmentSearchBinding
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

        binding.techWorkAnView.setFailureListener { t ->
            snackbarError(t.message.toString())
        }
        binding.techWorkAnView.setAnimation(R.raw.tech_work)

//        binding.accountImageSearch.setOnClickListener {
//            findNavController().navigate(R.id.action_searchFragment_to_profileFragment)
//        }
//    }
    }

//    private fun showProgressBar() {
//        binding.paginationProgressBar.visibility = View.VISIBLE
//    }
//
//    private fun hideProgressBar(){
//        binding.paginationProgressBar.visibility = View.INVISIBLE
//    }

    private fun snackbarError(error : String) {
        val rootView = view?.findViewById<ConstraintLayout>(R.id.search_root_layout)
        val duration = Snackbar.LENGTH_SHORT

        val snackbar = Snackbar
            .make(rootView!!, error, duration)
            .setBackgroundTint(resources.getColor(R.color.md_theme_light_errorContainer))
            .setTextColor(resources.getColor(R.color.md_theme_light_onErrorContainer))
        snackbar.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}