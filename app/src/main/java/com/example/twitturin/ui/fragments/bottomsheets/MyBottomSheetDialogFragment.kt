package com.example.twitturin.ui.fragments.bottomsheets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.twitturin.R
import com.example.twitturin.ui.activities.ReportActivity
import com.example.twitturin.ui.sealeds.FollowResult
import com.example.twitturin.viewmodel.FollowUserViewModel
import com.example.twitturin.viewmodel.manager.SessionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var followViewModel: FollowUserViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

        val usernameTv = view.findViewById<TextView>(R.id.b_username_tv)
        val followLayout = view.findViewById<LinearLayout>(R.id.follow_layout)
        val reportLayout = view.findViewById<LinearLayout>(R.id.report_post_layout)
        followViewModel = ViewModelProvider(requireActivity())[FollowUserViewModel::class.java]

        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val userId = sharedPreferences.getString("userId", "")

        usernameTv.text = "@$username"

        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.getToken()

        followLayout.setOnClickListener {
            followViewModel.followUsers(userId!!, "Bearer $token")
            dismiss()
        }

        followViewModel.followResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is FollowResult.Success -> {
                    Toast.makeText(requireContext(), "now you follow: $username", Toast.LENGTH_SHORT).show()
                }
                is FollowResult.Error -> {
                    val error = result.message
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        reportLayout.setOnClickListener {
            val intent = Intent(requireActivity(), ReportActivity::class.java)
            startActivity(intent)
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