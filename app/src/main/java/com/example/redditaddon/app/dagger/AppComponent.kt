package com.example.redditaddon.app.dagger

import android.app.Application
import com.example.redditaddon.App
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [
    NetworkModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance applicationContext: Application):AppComponent
    }
    fun inject(app: App)
}