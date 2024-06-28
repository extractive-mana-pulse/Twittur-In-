package com.example.twitturin.notification.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.BuildConfig
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentNotificationsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private val binding by lazy { FragmentNotificationsBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notificationsFragment = this

        binding.apply {

            notificationDownloadBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://objects.githubusercontent.com/github-production-release-asset-2e65be/733415969/eef51851-4613-4b6b-813e-baaf079092c5?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=releaseassetproduction%2F20240627%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240627T121633Z&X-Amz-Expires=300&X-Amz-Signature=0b843602fa4653288cadf96bf9501a5be77e4f2f0d32aa2102cfc76f568f3281&X-Amz-SignedHeaders=host&actor_id=0&key_id=0&repo_id=733415969&response-content-disposition=attachment%3B%20filename%3Dapp-debug.apk&response-content-type=application%2Fvnd.android.package-archive")
                startActivity(intent)
            }

            newUpdateLayout.setOnClickListener {

            }

        }
        checkAppVersion()
    }

    private fun checkAppVersion() {
        if (BuildConfig.VERSION_NAME < "2.0.2"){
            binding.newUpdateLayout.visibility = View.VISIBLE
            binding.emptyNotificationPageLayout.visibility = View.GONE
        } else {
            binding.newUpdateLayout.visibility = View.GONE
            binding.emptyNotificationPageLayout.visibility = View.VISIBLE
        }
    }
}