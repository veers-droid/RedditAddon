package com.example.redditaddon.app.dagger

import android.app.Application
import com.example.redditaddon.App
import com.example.redditaddon.activities.main.MainActivityCompose
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [
    NetworkModule::class,
ViewModelModule::class,
NetworkModule::class,
AppModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance applicationContext: Application):AppComponent
    }

    fun inject(app: App)

    fun inject(activity :  MainActivityCompose)
}