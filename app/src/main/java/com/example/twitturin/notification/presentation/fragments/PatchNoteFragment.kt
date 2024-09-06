package com.example.twitturin.notification.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
        }
    }
}