package com.example.twitturin.core.connection.observer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import io.reactivex.rxjava3.core.Observable

class ConnectivityObservable(private val context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observe(): Observable<Boolean> {
        return Observable.create { emitter ->
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    emitter.onNext(true)
                }

                override fun onLost(network: Network) {
                    emitter.onNext(false)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            } else {
                val builder = NetworkRequest.Builder()
                connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
            }

            emitter.setCancellable {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }
        }
    }

    fun stopObserving() {
        // Unregister the network callback
        connectivityManager.unregisterNetworkCallback(object : ConnectivityManager.NetworkCallback() {})
    }
}