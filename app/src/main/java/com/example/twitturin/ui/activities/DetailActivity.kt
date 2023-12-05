package com.example.twitturin.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.twitturin.databinding.ActivityDetailBinding
import com.example.twitturin.ui.fragments.bottomsheets.MyBottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class DetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fullname = intent.getStringExtra("fullname")
        val username = intent.getStringExtra("username")
        val description = intent.getStringExtra("post_description")
        val link = intent.getStringExtra("link")
        val createdTime = intent.getStringExtra("createdAt")
        val userId = intent.getStringExtra("userId")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        try {
            val date = dateFormat.parse(createdTime.toString())
            val currentTime = System.currentTimeMillis()

            val durationMillis = currentTime - date.time

            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
            val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
            val days = TimeUnit.MILLISECONDS.toDays(durationMillis)
            val weeks = days / 7

            val durationString = when {
                weeks > 0 -> "$weeks weeks ago"
                days > 0 -> "$days days ago"
                hours > 0 -> "$hours hours ago"
                minutes > 0 -> "$minutes minutes ago"
                else -> "$seconds seconds ago"
            }

            println("Post created $durationString")
            binding.whenCreated.text = durationString
        } catch (e: Exception) {
            println("Invalid date")
            binding.whenCreated.text = "Invalid date"
        }

        binding.apply {
            authorFullname.text = fullname
            authorUsername.text = "@$username"
            postDescription.text = description

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

            val elapsedTime = currentDateObj!!.time - createdDate!!.time
            val elapsedMinutes = elapsedTime / (60 * 1000)

            val formattedDate = createdDate.let { outputFormat.format(it) }

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