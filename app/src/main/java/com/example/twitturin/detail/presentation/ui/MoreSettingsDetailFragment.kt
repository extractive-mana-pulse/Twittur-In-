package com.example.twitturin.detail.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.util.snackbar
import com.example.twitturin.profile.presentation.util.snackbarError
import com.example.twitturin.detail.presentation.sealed.TweetDelete
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoreSettingsDetailFragment : BottomSheetDialogFragment() {

    @Inject lateinit var sessionManager: SessionManager
    private val tweetViewModel : TweetViewModel by viewModels()
    private val followViewModel : FollowViewModel by viewModels()

    @SuppressLint("SetTextI18n", "MissingInflatedId", "ShowToast")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

        val token = sessionManager.getToken()
        val userId2 = sessionManager.getUserId()

        val mainLayout = view.findViewById<LinearLayout>(R.id.bottom_sheet_root_layout)
        val usernameTv = view.findViewById<TextView>(R.id.b_username_tv)
        val followLayout = view.findViewById<LinearLayout>(R.id.follow_layout)
        val deleteLayout = view.findViewById<LinearLayout>(R.id.delete_layout)
        val editLayout = view.findViewById<LinearLayout>(R.id.edit_layout)
        val reportLayout = view.findViewById<LinearLayout>(R.id.report_post_layout)

        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val description = sharedPreferences.getString("post_description", "")
        val username = sharedPreferences.getString("username", "")
        val userId = sharedPreferences.getString("userId", "")
        val tweetId = sharedPreferences.getString("id", "")

        /**Checking user id. if match illustrate other ui otherwise don't. */
        if (userId == userId2) {
            followLayout.visibility = View.GONE
            editLayout.visibility = View.VISIBLE
            deleteLayout.visibility = View.VISIBLE
        } else {
            followLayout.visibility = View.VISIBLE
            deleteLayout.visibility = View.GONE
            editLayout.visibility = View.GONE
        }

        usernameTv.text = "@$username"

        followLayout.setOnClickListener {
            followViewModel.followUsers(userId!!, "Bearer $token")
            dismiss()
        }

        followViewModel.follow.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Follow.Success -> {
                    mainLayout.snackbar(
                        requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                        message = "now you follow: ${username?.uppercase()}"
                    )
                }
                is Follow.Error -> {
                    mainLayout.snackbarError(
                        requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                        error = result.message,
                        ""){}
                }
            }
        }

        deleteLayout.setOnClickListener {

            val alertDialogBuilder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            alertDialogBuilder.setTitle(resources.getString(R.string.delete_tweet_title))
            alertDialogBuilder.setMessage(resources.getString(R.string.delete_tweet_message))
            alertDialogBuilder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                tweetViewModel.deleteTweet(tweetId!!, "Bearer $token")
                findNavController().popBackStack()
                dismiss()
            }

            alertDialogBuilder.setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            tweetViewModel.deleteTweetResult.observe(viewLifecycleOwner){ result ->
                when(result){
                    is TweetDelete.Success -> {
                        mainLayout.snackbar(
                            requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                            message = resources.getString(R.string.deleted)
                        )

                    }
                    is  TweetDelete.Error -> {
                        mainLayout.snackbarError(
                            requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                            error = result.message,
                            ""){}
                    }
                }
            }
        }

        reportLayout.setOnClickListener {
//            mainLayout.snackbar(
//                requireActivity().findViewById(R.id.bottom_sheet_root_layout),
//                message = resources.getString(R.string.in_progress)
//            )
            findNavController().navigate(R.id.reportFragment)
            dismiss()
        }

        editLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putString("description", description)
            }
            findNavController().navigate(R.id.editTweetFragment, bundle)
            dismiss()
        }

        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog?
            val bottomSheet = bottomSheetDialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = bottomSheet?.let { it1 -> BottomSheetBehavior.from(it1) }
            behavior?.peekHeight = 800
        }
        return view
    }
}