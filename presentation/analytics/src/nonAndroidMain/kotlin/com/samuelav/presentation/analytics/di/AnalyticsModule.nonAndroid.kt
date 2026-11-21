package com.samuelav.presentation.analytics.di

import com.samuelav.presentation.analytics.base.LogScreenAnalytics
import com.samuelav.presentation.analytics.base.ScreenAnalytics
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformAnalyticsModule: Module = module {
    single<ScreenAnalytics> { LogScreenAnalytics() }
}
