package com.example.twitturin.core.extensions

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.twitturin.R
import com.google.android.material.appbar.MaterialToolbar

fun ImageView.loadImagesWithGlideExt(url: String?) {
    Glide.with(this)
        .load(url)
        .centerCrop()
        .error(R.drawable.sync_problem)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun MaterialToolbar.loadToolbarImage(url: String?, view: MaterialToolbar) {
    Glide.with(this)
        .load(url)
        .override(64,64)
        .circleCrop()
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) { view.navigationIcon = resource }

            override fun onLoadCleared(placeholder: Drawable?) {} })
}