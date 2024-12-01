package com.example.twitturin.core.extensions

import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.twitturin.profile.presentation.fragments.FullScreenImageFragment

fun Fragment.fullScreenImage(view : ImageView) {

    val fullScreenImageFragment = FullScreenImageFragment()

    view.buildDrawingCache()
    val originalBitmap = view.drawingCache
    val image = originalBitmap.config?.let { originalBitmap.copy(it, true) }

    val extras = Bundle()
    extras.putParcelable("image", image)
    fullScreenImageFragment.arguments = extras

    fullScreenImageFragment.show(requireActivity().supportFragmentManager, "FullScreenImageFragment")
}