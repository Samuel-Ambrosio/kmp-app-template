package com.samuelav.app

import com.samuelav.app.di.appComponent
import com.samuelav.app.utils.debug.KermitLogger
import com.samuelav.domain.model.utils.debug.AppLogger
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun startApp(platformDeclaration: KoinAppDeclaration = {}) {
    // TODO: Add the Crashlytics writer (androidMain, CrashlyticsLogWriter) after google-services.json file is added
    AppLogger.logger = KermitLogger()

    startKoin {
        platformDeclaration()
        modules(appComponent)
    }
}
