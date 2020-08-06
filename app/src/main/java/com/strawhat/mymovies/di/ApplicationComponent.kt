package com.strawhat.mymovies.di


import android.net.ConnectivityManager
import com.strawhat.mymovies.services.system.CustomNetworkCallBack
import com.strawhat.mymovies.vm.details.DetailsViewModel
import com.strawhat.mymovies.vm.main.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun connectivityManager(): ConnectivityManager

    fun networkCallBack(): CustomNetworkCallBack

    fun inject(viewModel: MainViewModel)

    fun inject(viewModel: DetailsViewModel)

}