package com.example.twitturin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.twitturin.MyPreferences
import com.example.twitturin.R
import com.example.twitturin.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController }
    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkTheme()
        loadLocale()

        binding.bottomNavView.setupWithNavController(navController)

        val fragmentsToHideBottomNav = setOf(
            R.id.detailFragment,
            R.id.signInFragment,
            R.id.studentRegistrationFragment,
            R.id.professorRegistrationFragment,
            R.id.editProfileFragment,
            R.id.privateMessagesFragment,
            R.id.kindFragment,
            R.id.profileFragment,
            R.id.followersListFragment,
            R.id.followingListFragment,
            R.id.publicPostFragment,
            R.id.fullScreenImageFragment,
            R.id.reportFragment,
            R.id.editTweetFragment,

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
}