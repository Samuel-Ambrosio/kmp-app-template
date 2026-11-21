package com.samuelav.app.utils.analytics

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CancellationException

class CrashlyticsLogWriter(private val firebaseCrashlytics: FirebaseCrashlytics) : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        firebaseCrashlytics.log(message)

        if (severity == Severity.Error && throwable != null && throwable !is CancellationException) {
            firebaseCrashlytics.recordException(throwable)
        }
    }
}
