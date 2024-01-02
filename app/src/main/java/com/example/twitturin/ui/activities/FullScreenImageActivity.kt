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

        val imageBitmap = intent.getParcelableExtra<Bitmap>("image")
        binding.apply {

            imageFullScreenArticle.setImageBitmap(imageBitmap)

            fullScreenArticlePageBackBtn.setOnClickListener {
                onBackPressed()
            }
        }
    }
}