package com.example.redditaddon

import android.app.Application
import com.example.redditaddon.app.dagger.AppComponent
import com.example.redditaddon.app.dagger.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)

        appComponent.inject(this)
    }

}