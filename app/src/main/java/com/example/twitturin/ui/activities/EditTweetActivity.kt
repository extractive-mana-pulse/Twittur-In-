package com.example.twitturin.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.ActivityEditTweetBinding
import com.example.twitturin.databinding.FragmentEditTweetBinding
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.ui.sealeds.EditTweetResult
import com.example.twitturin.ui.sealeds.EditUserResult
import com.example.twitturin.viewmodel.ProfileViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditTweetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTweetBinding

    private val profileViewModel: ProfileViewModel by viewModels()

    @Inject lateinit var snackbarHelper: SnackbarHelper

    @Inject lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTweetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = sessionManager.getToken()

        val description = intent.getStringExtra("post_description")
        val tweetId = intent.getStringExtra("id")

        binding.editTweetContent.setText(description)

        binding.editTweetCancelBtn.setOnClickListener {
            finish()
        }
        binding.editTweetPublishBtn.setOnClickListener {
            binding.editTweetPublishBtn.isEnabled = false
            val content = binding.editTweetContent.text.toString()
            profileViewModel.editTweet(content, tweetId!!, "Bearer $token")
        }

        profileViewModel.editTweetResult.observe(this) { result ->
            when (result) {
                is EditTweetResult.Success -> {
                    finish()
                    Toast.makeText(this, "Succeed", Toast.LENGTH_SHORT).show()
                }

                is EditTweetResult.Error -> {
                    snackbarHelper.snackbarError(
                        findViewById<ConstraintLayout>(R.id.edit_tweet_root_layout),
                        binding.forSnackbar,
                        error = result.error,
                        ""){}
                    binding.editTweetPublishBtn.isEnabled = true
                }
            }
        }
    }
}