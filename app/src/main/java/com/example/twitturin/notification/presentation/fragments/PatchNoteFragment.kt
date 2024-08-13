package com.example.twitturin.notification.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.databinding.FragmentPatchNoteBinding
import com.example.twitturin.notification.presentation.vm.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PatchNoteFragment : Fragment() {

    private val notificationViewModel by viewModels<NotificationViewModel>()
    private val binding by lazy { FragmentPatchNoteBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            patchNoteToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            repeatOnStarted {
                notificationViewModel.getLatestRelease()
                notificationViewModel.gitResponse.collectLatest {
                    if (it != null) {
                        if (it.isSuccessful) {
                            it.body()?.apply {
                                Markwon.create(requireContext()).setMarkdown(patchNoteTv, body.toString())
                                patchNotPageUpdateBtn.setOnClickListener {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse(assets[0].browserDownloadUrl)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }

//            Markwon.create(requireContext()).setMarkdown(patchNoteTv, "## UI/UX Improvements\n" +
//                    "\n" +
//                    "* Home screen bottom bar now has title\n" +
//                    "* Create post Button shape changed\n" +
//                    "* Detail page, Reply Edit text reworked. Now it's flexible under size of user text\n" +
//                    "* Create post page rebuild\n" +
//                    "\n" +
//                    "## Optimization\n" +
//                    "\n" +
//                    "* Mostly done code optimization so user cannot see that but there is noticeable improvements in performance and animations\n" +
//                    "\n" +
//                    "## Features\n" +
//                    "\n" +
//                    "* Feedback system implemented. Now user can leave a feedback!\n" +
//                    "* Report system implemented. Now user can report a post!\n" +
//                    "* Policy of creating post added (template only!) Will be modified soon.\n" +
//                    "* Search function implemented.\n" +
//                    "* Report page has Other radio button. Gives users opportunity to leave their own report.\n" +
//                    "\n" +
//                    "## Bug Fixes\n" +
//                    "\n" +
//                    "* Fixed list of navigation bugs\n" +
//                    "* Fixed search logic. Now when user search another user will be shown correct result\n" +
//                    "* Fixed bug when user tries to edit his post from profile page but context of post is wrong")
        }
    }
}