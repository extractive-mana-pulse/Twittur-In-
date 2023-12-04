package com.example.twitturin.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.twitturin.databinding.ActivityDetailBinding
import com.example.twitturin.ui.fragments.bottomsheets.MyBottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class DetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fullname = intent.getStringExtra("fullname")
        val username = intent.getStringExtra("username")
        val description = intent.getStringExtra("post_description")
        val link = intent.getStringExtra("link")
        val createdTime = intent.getStringExtra("createdAt")


//        val formattedText = createdTime?.let { formatCreatedAt(it) }

        binding.apply {
            authorFullname.text = fullname
            authorUsername.text = "@$username"
            postDescription.text = description
            whenCreated.text = createdTime?.let { setTweetCreatedAt(it) }

            followBtn.setOnClickListener {
                Toast.makeText(
                    this@DetailActivity,
                    "actualnost birinchi orinda!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            goBackBtn.setOnClickListener {
                onBackPressed()
            }

            moreSettings.setOnClickListener {
                val bottomSheetDialogFragment = MyBottomSheetDialogFragment()
                bottomSheetDialogFragment.show(
                    supportFragmentManager,
                    "MyBottomSheetDialogFragment"
                )
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentlyDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(Date())
    }

    private fun setTweetCreatedAt(createdAt: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val currentDate = getCurrentlyDateTime()

        return try {
            val createdDate = inputFormat.parse(createdAt)
            val currentDateObj = inputFormat.parse(currentDate)

            val elapsedTime = currentDateObj.time - createdDate.time
            val elapsedMinutes = elapsedTime / (60 * 1000)

            val formattedDate = outputFormat.format(createdDate)

            val timeAgo = when {
                elapsedMinutes < 1 -> "just now"
                elapsedMinutes == 1L -> "1 minute ago"
                elapsedMinutes < 60 -> "$elapsedMinutes minutes ago"
                elapsedMinutes < 24 * 60 -> {
                    val hours = elapsedMinutes / 60
                    "$hours hours ago"
                }
                else -> outputFormat.format(createdDate)
            }

            "$formattedDate ($timeAgo)"
        } catch (e: Exception) {
            e.printStackTrace()
            createdAt
        }
    }
}