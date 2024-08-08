package com.example.twitturin.core.connection.observer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import io.reactivex.rxjava3.core.Observable

class ConnectivityObservable(context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            emitter.setCancellable { connectivityManager.unregisterNetworkCallback(networkCallback) }
        }
    }
    fun stopObserving() { connectivityManager.unregisterNetworkCallback(object : ConnectivityManager.NetworkCallback() {}) }
}