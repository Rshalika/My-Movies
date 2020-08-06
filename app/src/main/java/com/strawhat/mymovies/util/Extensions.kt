package com.strawhat.mymovies.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

fun Context.isNetworkConnected(): Boolean {
    return checkInternetConnection(context = this)
}

private fun checkInternetConnection(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}