package com.example.twitturin.ui.fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentFullScreenImageBinding

class FullScreenImageFragment : Fragment() {

    private lateinit var imageUrl: String
    private lateinit var binding : FragmentFullScreenImageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFullScreenImageBinding.inflate(layoutInflater)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val extras = arguments
        val bmp = extras?.getParcelable<Bitmap>("image")

        binding.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.imageFullScreen.setImageBitmap(bmp)
    }


//    companion object {
//        private const val ARG_IMAGE_URL = "image_url"
//
//        fun newInstance(imageUrl: String): FullScreenImageFragment {
//            val fragment = FullScreenImageFragment()
//            val args = Bundle()
//            args.putString(ARG_IMAGE_URL, imageUrl)
//            fragment.arguments = args
//            return fragment
//        }
//    }
}