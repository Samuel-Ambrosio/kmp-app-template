package com.samuelav.presentation.analytics.di

import org.koin.core.module.Module
import org.koin.dsl.module

val analyticsModule = module {
    includes(platformAnalyticsModule)
}

internal expect val platformAnalyticsModule: Module
