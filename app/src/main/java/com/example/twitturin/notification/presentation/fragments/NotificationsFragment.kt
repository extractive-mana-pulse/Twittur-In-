package com.example.twitturin.notification.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.repeatOnStarted
import com.example.twitturin.core.helper.checkForUpdates
import com.example.twitturin.databinding.FragmentNotificationsBinding
import com.example.twitturin.notification.presentation.sealed.NotificationUIEvent
import com.example.twitturin.notification.presentation.vm.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private val notificationViewModel by viewModels<NotificationViewModel>()
    private val binding by lazy { FragmentNotificationsBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            notificationViewModel.getLatestRelease()
            repeatOnStarted {
                notificationViewModel.gitResponse.collectLatest { response ->
                    if (response != null) {
                        if (response.isSuccessful) {
                            response.body()?.apply {

                                requireActivity().checkForUpdates(
                                    tagName.toString(),
                                    notificationPageAnView,
                                    emptyNotificationPageLayout,
                                    downloadLayout
                                )

                                notificationUpdateTitle.text = name
                                downloadImage.setImageResource(R.drawable.logo)

                                downloadLayout.setOnClickListener { notificationViewModel.onItemPressed() }
                                notificationDownloadBtn.setOnClickListener { notificationViewModel.onDownloadPressed() }

                                viewLifecycleOwner.lifecycleScope.launch {
                                    notificationViewModel.notificationEvent.collect { event ->
                                        when(event){
                                            NotificationUIEvent.OnDownloadPressed -> {
                                                val intent = Intent(Intent.ACTION_VIEW)
                                                intent.data = Uri.parse(assets[0].browserDownloadUrl)
                                                startActivity(intent)
                                            }
                                            NotificationUIEvent.OnItemPressed -> { findNavController().navigate(R.id.action_notificationsFragment_to_patchNoteFragment) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}