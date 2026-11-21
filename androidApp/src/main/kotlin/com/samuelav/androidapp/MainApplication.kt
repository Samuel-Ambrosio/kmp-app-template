package com.samuelav.androidapp

import android.app.Application
import com.samuelav.app.startApp
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // TODO: Remove '//' after google-services.json file is added
        // FirebaseApp.initializeApp(this)

        startApp { androidContext(this@MainApplication) }
    }
}
