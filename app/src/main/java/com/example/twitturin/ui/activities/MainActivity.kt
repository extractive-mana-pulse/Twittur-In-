package com.example.twitturin.ui.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.twitturin.R
import com.example.twitturin.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController }
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
            R.id.profileFragment,
            R.id.followersListFragment,
            R.id.followingListFragment,

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