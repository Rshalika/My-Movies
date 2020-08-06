package com.strawhat.mymovies

import android.app.Application
import android.net.NetworkRequest
import com.strawhat.mymovies.di.ApplicationComponent
import com.strawhat.mymovies.di.ApplicationModule
import com.strawhat.mymovies.di.DaggerApplicationComponent

class MyApplication : Application() {

    val appComponent: ApplicationComponent =
        DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()

    override fun onCreate() {
        super.onCreate()
        appComponent.connectivityManager().registerNetworkCallback(
            NetworkRequest.Builder().build(),
            appComponent.networkCallBack()
        )
    }


}