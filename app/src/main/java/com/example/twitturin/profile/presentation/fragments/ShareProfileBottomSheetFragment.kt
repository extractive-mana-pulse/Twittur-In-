package com.example.twitturin.profile.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import com.example.twitturin.core.extensions.copyToClipboard
import com.example.twitturin.core.extensions.loadImagesWithGlideExt
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.FragmentShareProfileBottomSheetBinding
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private val profileViewModel : ProfileViewModel by viewModels()
    private val binding by lazy { FragmentShareProfileBottomSheetBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = SessionManager(requireContext()).getUserId()

        // TODO: outer layout of share UI should be linked with color of user chosen from colorPickerDialog.

        binding.apply {

            profileShareToolbar.setNavigationOnClickListener { dismiss() }

            profileViewModel.getUserCredentials(userId!!)
            profileViewModel.getUserCredentials.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is UserCredentials.Success -> {

                        kindTv.text = result.user.kind
                        studentIdTv.text = result.user.studentId
                        shareProfileFullName.text = result.user.fullName
                        shareProfileUsername.text = "@${result.user.username}"
                        userAvatarShare.loadImagesWithGlideExt(result.user.profilePicture)
                    }

                    is UserCredentials.Error -> { standardBottomSheet.snackbarError(standardBottomSheet, error = result.message, "") { } }
                }
            }

            shareProfileLayout.setOnClickListener { requireContext().shareUrl("https://twitturin.onrender.com/users/$userId") }
            copyLinkLayout.setOnClickListener { requireContext().copyToClipboard("https://twitturin.onrender.com/users/$userId") }

        }

        val bottomSheet : FrameLayout = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!

        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            peekHeight = resources.displayMetrics.heightPixels
            state = BottomSheetBehavior.STATE_EXPANDED

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }
}