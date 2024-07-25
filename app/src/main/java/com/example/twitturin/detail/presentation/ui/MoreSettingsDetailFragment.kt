package com.example.twitturin.detail.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.core.extensions.beVisible
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.databinding.BottomSheetLayoutBinding
import com.example.twitturin.detail.presentation.sealed.TweetDelete
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreSettingsDetailFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLayoutBinding? = null
    private val binding get() = _binding!!
    private val tweetViewModel by viewModels<TweetViewModel>()
    private val followViewModel by viewModels<FollowViewModel>()

    @SuppressLint("SetTextI18n", "MissingInflatedId", "ShowToast")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val replyLayout = requireActivity().findViewById<ConstraintLayout>(R.id.reply_layout)
        val bottomNavLayout = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val args = MoreSettingsDetailFragmentArgs.fromBundle(requireArguments())
        val token = SessionManager(requireContext()).getToken()

        /**Checking user id. if match illustrate other ui otherwise don't. */
        if (args.tweet.author?.id == SessionManager(requireContext()).getUserId()) {
            binding.followLayout.beGone()
            binding.editLayout.beVisible()
            binding.deleteLayout.beVisible()
        } else {
            binding.followLayout.beVisible()
            binding.editLayout.beGone()
            binding.deleteLayout.beGone()
        }

        binding.bUsernameTv.text = "@${args.tweet.author?.username}"

        binding.followLayout.setOnClickListener {
            followViewModel.followUsers(args.tweet.author?.id!!, "Bearer $token")
            followViewModel.follow.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Follow.Success -> {
                        dismiss()
                        replyLayout.snackbar(
                            replyLayout,
                            message = "now you follow: ${args.tweet.author?.username?.uppercase()}"
                        )
                    }
                    is Follow.Error -> {
                        replyLayout.snackbarError(
                            replyLayout,
                            error = result.message,
                            ""){}
                    }
                }
            }
        }

        // delete not working properly.
        binding.deleteLayout.setOnClickListener {

            val alertDialogBuilder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            alertDialogBuilder.apply {
                setTitle(resources.getString(R.string.delete_tweet_title))
                setMessage(resources.getString(R.string.delete_tweet_message))

                setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    tweetViewModel.deleteTweet(args.tweet.id!!, "Bearer $token")
                    findNavController().navigateUp()
                    tweetViewModel.deleteTweetResult.observe(viewLifecycleOwner){ result ->
                        when(result){

                            is TweetDelete.Success -> {
                                replyLayout.snackbar(
                                    replyLayout,
                                    message = resources.getString(R.string.deleted)
                                )
                                dismiss()
                            }

                            is  TweetDelete.Error -> {
                                replyLayout.snackbarError(
                                    replyLayout,
                                    error = result.message,
                                    ""){}
                            }
                        }
                    }
                }

                setNegativeButton(resources.getString(R.string.no)) { _, _ -> dismiss() }

                val alertDialog = create()
                alertDialog.show()
            }
        }

        binding.reportPostLayout.setOnClickListener {
            findNavController().navigate(R.id.reportFragment)
            dismiss()
        }

        binding.editLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putString("description", args.tweet.content)
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
    }
}