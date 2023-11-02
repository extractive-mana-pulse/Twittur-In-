package com.example.twitturin.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.presentation.PostItem
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentHomeBinding
import com.example.twitturin.presentation.adapters.PostAdapter

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

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

        val postLists = listOf(
            PostItem("Apple", R.drawable.apple, "New Iphone 15 Pro Max realized"),
            PostItem("Amazon", R.drawable.amazon, "New GPU from NVIDIA now available to purchase in our stock"),
            PostItem("ASUS", R.drawable.asus, "New Rtx 4090 with water cooling system design will be realized at 12.01.2024"),
            PostItem("NVIDIA", R.drawable.nvidia, "DLSS 3.0 now available on RTX-30 series GPU's"),
            PostItem("Tesla", R.drawable.tesla, "New Tesla model now in stock."),
            PostItem("Google", R.drawable.google, "New Android 14 & UI 6.1 are available to download on this list of phones "),
            PostItem("MI", R.drawable.xiaomi, "Our New HyperOS will be realised next year"),
            PostItem("X", R.drawable.cross, "Now we will not ban you for N word if you purchase premium version of our app. It's cost only 11.99 $"),
        )

        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PostAdapter(postLists)
        binding.rcView.adapter = adapter

    }

    fun goToWebView(){
        findNavController().navigate(R.id.webViewFragment)
    }

    fun goToProfile(){
        findNavController().navigate(R.id.profileFragment)
    }

    fun add(){
        findNavController().navigate(R.id.publicPostFragment)
    }

    fun testImage(){
        findNavController().navigate(R.id.signInFragment)
    }
    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}