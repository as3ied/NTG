package com.example.currency.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class
App:Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            // Android context
            androidContext(this@App)
            // modules
            modules(appModule)
        }
    }
}