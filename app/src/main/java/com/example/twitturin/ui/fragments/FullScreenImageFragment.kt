package com.example.twitturin.ui.fragments

import android.graphics.Bitmap
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.databinding.FragmentFullScreenImageBinding

@Suppress("DEPRECATION")
class FullScreenImageFragment : Fragment() {

    private val binding by lazy { FragmentFullScreenImageBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val extras = arguments
            val bmp = extras?.getParcelable<Bitmap>("image")

            btnClose.setOnClickListener {
                findNavController().popBackStack()
            }

            imageFullScreen.setImageBitmap(bmp)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                testLayout.setRenderEffect(RenderEffect.createBlurEffect(50F, 50F, Shader.TileMode.CLAMP))
            }
        }
    }
}