package com.strawhat.mymovies.services.system

import android.content.Context
import com.jakewharton.rxrelay3.BehaviorRelay
import com.strawhat.mymovies.util.isNetworkConnected
import io.reactivex.rxjava3.core.Observable

class SystemInfoService(private val context: Context) {
    private val networkStateRelay =
        BehaviorRelay.createDefault(convertToNetworkState(context.isNetworkConnected()))

    fun updateState(networkState: NetworkState) {
        networkStateRelay.accept(networkState)
    }

    fun getNetworkState(): Observable<NetworkState> {
        return networkStateRelay
    }

    private fun convertToNetworkState(connected: Boolean): NetworkState =
        if (connected) NetworkState.CONNECTED else NetworkState.DISCONNECTED
}