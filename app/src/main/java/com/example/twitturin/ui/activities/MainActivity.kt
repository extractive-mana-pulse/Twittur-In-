package com.example.twitturin.ui.activities

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.twitturin.R
import com.example.twitturin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController }
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> { navController.navigate(R.id.homeFragment) }
                R.id.search -> { navController.navigate(R.id.searchFragment) }
                R.id.notification -> { navController.navigate(R.id.notificationsFragment) }
                R.id.messages -> { navController.navigate(R.id.privateMessagesFragment) }
            }
            true
        }

        val fragmentsToHideBottomNav = setOf(
            R.id.signInFragment,
            R.id.studentRegistrationFragment,
            R.id.professorRegistrationFragment,
            R.id.editProfileFragment,
            R.id.privateMessagesFragment,
            R.id.kindFragment,
            R.id.profileFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in fragmentsToHideBottomNav) {
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.bottomNavView.visibility = View.VISIBLE
            }
        }
    }
}