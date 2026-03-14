package com.example.footballscout

import android.app.Application
import com.example.footballscout.di.AppContainer

class FootballScoutApp : Application() {
    
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
