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

//        if (isDarkModeActive()){
//            window.statusBarColor = ContextCompat.getColor(this, com.google.android.material.R.color.m3_sys_color_dark_surface_container)
//            window.decorView.windowInsetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
//
//        } else {
//            window.statusBarColor = ContextCompat.getColor(this, com.google.android.material.R.color.m3_sys_color_light_surface_container)
//            window.decorView.windowInsetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
//        }

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
    private fun isDarkModeActive(): Boolean {
        return when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

//    override fun onStop() {
//        super.onStop()
//        val preferences = getSharedPreferences("checkbox", MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = preferences.edit()
//        editor.putString("remember","false")
//        editor.clear()
//        editor.apply()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        val preferences = getSharedPreferences("checkbox", MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = preferences.edit()
//        editor.putString("remember","false")
//        editor.clear()
//        editor.apply()
//    }
//    override fun onSupportNavigateUp(): Boolean {
//        return super.onSupportNavigateUp()
//    }
}