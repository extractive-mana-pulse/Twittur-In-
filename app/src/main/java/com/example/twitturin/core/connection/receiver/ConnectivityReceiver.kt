package com.example.twitturin.core.connection.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.twitturin.core.extensions.isNetworkAvailable

class ConnectivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (context.isNetworkAvailable()) {
            Log.d("Internet status", "Internet connection available")
        } else {
            Log.d("Internet status", "Internet connection not available")
        }
    }
}