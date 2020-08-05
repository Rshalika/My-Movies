package com.strawhat.mymovies.di


import com.strawhat.mymovies.vm.details.DetailsViewModel
import com.strawhat.mymovies.vm.main.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(viewModel: MainViewModel)

    fun inject(viewModel: DetailsViewModel)

}