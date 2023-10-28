package com.example.twitturin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> { navController.navigate(R.id.homeFragment) }
                R.id.search -> { navController.navigate(R.id.searchFragment) }
                R.id.notification -> { navController.navigate(R.id.notificationsFragment) }
                R.id.messages -> { /* chat Fragment will be added later. this fragment like a private message fragment */ }
            }
            true
        }
    }
}