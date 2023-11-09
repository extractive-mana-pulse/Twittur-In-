package com.example.twitturin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.twitturin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController }
    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isDarkModeActive()){
            window.statusBarColor = ContextCompat.getColor(this, com.google.android.material.R.color.m3_sys_color_dark_surface_container)
        } else {
            window.statusBarColor = ContextCompat.getColor(this, com.google.android.material.R.color.m3_sys_color_light_surface_container)
        }

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
            R.id.signUpFragment,
            R.id.editProfileFragment,
            R.id.privateMessagesFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in fragmentsToHideBottomNav) {
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.bottomNavView.visibility = View.VISIBLE
            }
        }
    }
    private fun isDarkModeActive(): Boolean {
        return when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}