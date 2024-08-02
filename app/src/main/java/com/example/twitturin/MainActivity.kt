package com.example.twitturin

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.twitturin.core.extensions.bottomNavigationUI
import com.example.twitturin.core.extensions.checkStatus
import com.example.twitturin.core.extensions.checkTheme
import com.example.twitturin.core.extensions.loadLocale
import com.example.twitturin.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import leakcanary.LeakCanary

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController }

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }

        this.checkTheme()
        this.loadLocale()
        this.checkStatus(binding.bottomNavView)
        Log.d("leak", LeakCanary.toString())
        bottomNavigationUI(binding.bottomNavView)
        binding.bottomNavView.setupWithNavController(navController)
    }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle(resources.getString(R.string.notification_title))
            .setMessage(resources.getString(R.string.notification_message))
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package: TwitturIn")
                startActivity(intent)
            }
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .show()
    }

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

        hasNotificationPermissionGranted = isGranted
        if (!isGranted) {
            if (Build.VERSION.SDK_INT >= 33) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                    showNotificationPermissionRationale()
                } else {
                    showSettingDialog()
                }
            }
        } else {
            Log.d("permission","granted")
        }
    }

    private fun showNotificationPermissionRationale() {

        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle(resources.getString(R.string.alert))
            .setMessage(resources.getString(R.string.notification_require_message))
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .show()
    }
    private var hasNotificationPermissionGranted = false
}