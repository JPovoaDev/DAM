package com.example.splitexpenses

import android.app.Application
import com.example.splitexpenses.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SplitExpensesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SplitExpensesApp)
            modules(appModule)
        }
    }
}
