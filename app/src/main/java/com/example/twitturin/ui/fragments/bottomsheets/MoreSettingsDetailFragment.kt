package com.example.twitturin.ui.fragments.bottomsheets

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.ui.sealeds.FollowResult
import com.example.twitturin.viewmodel.FollowUserViewModel
import com.example.twitturin.viewmodel.ProfileViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoreSettingsDetailFragment : BottomSheetDialogFragment() {

    private lateinit var followViewModel: FollowUserViewModel
    private lateinit var profileViewModel: ProfileViewModel
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var snackbarHelper: SnackbarHelper

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

        val token = sessionManager.getToken()
        val userId2 = sessionManager.getUserId()

        val usernameTv = view.findViewById<TextView>(R.id.b_username_tv)
        val followLayout = view.findViewById<LinearLayout>(R.id.follow_layout)
        val deleteLayout = view.findViewById<LinearLayout>(R.id.delete_layout)
        val reportLayout = view.findViewById<LinearLayout>(R.id.report_post_layout)
        profileViewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        followViewModel = ViewModelProvider(requireActivity())[FollowUserViewModel::class.java]

        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val userId = sharedPreferences.getString("userId", "")
        val tweetId = sharedPreferences.getString("id", "")

        if (userId == userId2) {
            followLayout.visibility = View.GONE
            followLayout.visibility = View.GONE
            deleteLayout.visibility = View.VISIBLE
            deleteLayout.visibility = View.VISIBLE
        } else {
            followLayout.visibility = View.VISIBLE
            followLayout.visibility = View.VISIBLE
            deleteLayout.visibility = View.GONE
            deleteLayout.visibility = View.GONE
        }

        usernameTv.text = "@$username"

        followLayout.setOnClickListener {
            followViewModel.followUsers(userId!!, "Bearer $token")
            dismiss()
        }

        followViewModel.followResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is FollowResult.Success -> {
                    snackbarHelper.snackbar(
                        requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                        requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                        message = "now you follow: ${username?.uppercase()}"
                    )
                }
                is FollowResult.Error -> {
                    snackbarHelper.snackbarError(
                        requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                        requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                        result.message,
                        ""){}
                }
            }
        }

        deleteLayout.setOnClickListener {

            val alertDialogBuilder = AlertDialog.Builder(requireActivity())
            alertDialogBuilder.setTitle("Are you sure you want to delete the post?")
            alertDialogBuilder.setMessage("Please note that once you delete a publication it cannot be restored")
            alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                profileViewModel.deleteTweet( tweetId!!,"Bearer $token")
            }

            alertDialogBuilder.setNegativeButton("No") { _, _ ->
                dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            profileViewModel.deleteTweetResult.observe(viewLifecycleOwner){ result ->
                when(result){
                    is DeleteResult.Success -> {
                        snackbarHelper.snackbar(
                            requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                            requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                            message = "Deleted"
                        )
                        findNavController().navigate(R.id.homeFragment)
                    }
                    is  DeleteResult.Error -> {
                        snackbarHelper.snackbarError(
                            requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                            requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                            result.message,
                            ""){}
                    }
                }
            }
        }

        reportLayout.setOnClickListener {
            snackbarHelper.snackbar(
                requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                requireActivity().findViewById(R.id.bottom_sheet_root_layout),
                message = "In Progress"
            )
//            TODO { when report a post end point is ready activate this code }
//            val intent = Intent(requireActivity(), ReportActivity::class.java)
//            startActivity(intent)
//            dismiss()

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