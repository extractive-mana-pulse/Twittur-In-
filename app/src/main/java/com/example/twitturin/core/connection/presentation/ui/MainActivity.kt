package com.example.twitturin.core.connection.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.twitturin.R
import com.example.twitturin.core.connection.observer.ConnectivityObservable
import com.example.twitturin.core.extensions.bottomNavigationUI
import com.example.twitturin.core.extensions.checkStatus
import com.example.twitturin.core.extensions.checkTheme
import com.example.twitturin.core.extensions.loadLocale
import com.example.twitturin.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var isConnected = false
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val connectivityObservable by lazy { ConnectivityObservable(this) }
    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController }

    @SuppressLint("PrivateResource", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        connectivityObservable.observe()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { connected ->
                isConnected = connected
                if (isConnected) { Log.d("Internet status", "Connected") } else { navController.navigate(R.id.noInternetFragment) }
            }

        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }

        binding.apply {
            bottomNavigationUI(bottomNavView)
            this@MainActivity.checkStatus(bottomNavView)
            bottomNavView.setupWithNavController(navController)
        }
//        LeakCanary
        this.checkTheme()
        this.loadLocale()
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

    override fun onPause() { super.onPause();connectivityObservable.stopObserving() }
    override fun onDestroy() { super.onDestroy();connectivityObservable.stopObserving() }
}