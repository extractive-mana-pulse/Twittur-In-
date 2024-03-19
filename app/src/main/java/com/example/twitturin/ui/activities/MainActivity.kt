package com.example.twitturin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.twitturin.R
import com.example.twitturin.auth.vm.StayInViewModel
import com.example.twitturin.databinding.ActivityMainBinding
import com.example.twitturin.notification.presentation.fragments.NotificationsFragment
import com.example.twitturin.preferences.MyPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var stayInViewModel : StayInViewModel
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController }

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fragmentToOpen = intent.getStringExtra("fragment")
        if (fragmentToOpen == "notifications") {
            navController.navigate(R.id.action_homeFragment_to_notificationFragment)
        }

        stayInViewModel = ViewModelProvider(this)[StayInViewModel::class.java]

        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }

        checkTheme()
        loadLocale()

        binding.bottomNavView.setupWithNavController(navController)

        val fragmentsToHideBottomNav = setOf(
            R.id.detailFragment,
            R.id.signInFragment,
            R.id.studentRegistrationFragment,
            R.id.professorRegistrationFragment,
            R.id.editProfileFragment,
            R.id.kindFragment,
            R.id.profileFragment,
            R.id.followersListFragment,
            R.id.followingListFragment,
            R.id.publicPostFragment,
            R.id.fullScreenImageFragment,
            R.id.reportFragment,
            R.id.editTweetFragment,
            R.id.stayInFragment,
            R.id.noInternetFragment,

        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in fragmentsToHideBottomNav) {
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.bottomNavView.visibility = View.VISIBLE
            }
        }
    }

    private fun checkTheme() {
        when (MyPreferences(this).darkMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun setLocale(lang : String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("lang", lang)
        editor.apply()
    }

    private fun loadLocale() {
        val sharedPref = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPref.getString("lang","")
        language?.let { setLocale(it) }
    }
    // в первую очередь нужно проверить зарегистрирован пользователь или нет. если заре
    // если пользователь зареган после провесь соединение. если соединение установлено!
    // выполни навигацию на главный экран. если соединение не установлено но покажи экран с ошибкой интернета
    //

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package: TwitturIn")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
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
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private var hasNotificationPermissionGranted = false
}