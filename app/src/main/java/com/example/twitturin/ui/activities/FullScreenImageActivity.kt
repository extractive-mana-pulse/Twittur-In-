package com.example.twitturin.ui.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsetsController
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.twitturin.R
import com.example.twitturin.databinding.ActivityDetailBinding
import com.example.twitturin.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFullScreenImageBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (isDarkModeActive()){
            window.statusBarColor = ContextCompat.getColor(this, com.google.android.material.R.color.m3_sys_color_dark_surface_container)
            window.decorView.windowInsetsController?.setSystemBarsAppearance(0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )

        } else {
            window.statusBarColor = ContextCompat.getColor(this, com.google.android.material.R.color.m3_sys_color_light_surface_container)
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val imageBitmap = intent.getParcelableExtra<Bitmap>("image")
        binding.apply {

            imageFullScreenArticle.setImageBitmap(imageBitmap)

            fullScreenArticlePageBackBtn.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun isDarkModeActive(): Boolean {
        return when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}