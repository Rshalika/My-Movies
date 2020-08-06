package com.strawhat.mymovies.services.system

import android.net.ConnectivityManager
import android.net.Network

class CustomNetworkCallBack(private val systemStateRepository: SystemInfoService) :
    ConnectivityManager.NetworkCallback() {


    override fun onLost(network: Network) {
        super.onLost(network)
        systemStateRepository.updateState(NetworkState.DISCONNECTED)
    }

    override fun onUnavailable() {
        super.onUnavailable()
        systemStateRepository.updateState(NetworkState.DISCONNECTED)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        systemStateRepository.updateState(NetworkState.CONNECTED)
    }
}