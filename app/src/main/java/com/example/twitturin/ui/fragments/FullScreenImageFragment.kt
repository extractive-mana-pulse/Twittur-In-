package com.example.twitturin.ui.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.databinding.FragmentFullScreenImageBinding

@Suppress("DEPRECATION")
class FullScreenImageFragment : Fragment() {

    private lateinit var binding : FragmentFullScreenImageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFullScreenImageBinding.inflate(layoutInflater)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val extras = arguments
        val bmp = extras?.getParcelable<Bitmap>("image")

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageFullScreen.setImageBitmap(bmp)
    }
}