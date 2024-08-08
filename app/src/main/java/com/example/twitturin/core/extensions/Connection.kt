package com.example.twitturin.core.extensions

import android.content.Context
import android.net.ConnectivityManager
import androidx.navigation.NavController
import com.example.twitturin.R

fun Context.checkConnection(navController: NavController) {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo

    if (networkInfo != null && networkInfo.isConnected) {
        navController.navigate(R.id.signInFragment)
    } else {
        navController.navigate(R.id.noInternetFragment)
    }
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}