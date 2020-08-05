package com.strawhat.mymovies

import android.app.Application
import com.strawhat.mymovies.di.DaggerApplicationComponent

class MyApplication : Application() {

    val appComponent = DaggerApplicationComponent.create()
}