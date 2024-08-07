package com.example.twitturin.core.connection.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            Toast.makeText(context, "Internet connection available", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Internet connection not available", Toast.LENGTH_SHORT).show()
        }
    }
}