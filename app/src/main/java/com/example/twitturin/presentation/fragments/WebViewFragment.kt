package com.example.twitturin.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {

    private lateinit var binding : FragmentWebViewBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWebViewBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.loadUrl("https://ttpu.edupage.org/timetable/")
        binding.webView.settings.javaScriptEnabled = true
    }

    companion object {
        @JvmStatic
        fun newInstance() = WebViewFragment()
    }
}