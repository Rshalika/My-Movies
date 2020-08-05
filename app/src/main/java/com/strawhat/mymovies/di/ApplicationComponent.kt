package com.strawhat.mymovies.di


import com.strawhat.mymovies.vm.MainViewModel
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(viewModel: MainViewModel)

}