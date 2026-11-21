package com.samuelav.presentation.analytics.base

import com.samuelav.domain.model.utils.debug.AppLogger

internal class LogScreenAnalytics : ScreenAnalytics {
    override fun notifyAction(action: AnalyticsAction?) {
        action ?: return
        AppLogger.d("Analytics event: ${action.eventName} ${action.parameters.associate { it.name to it.value }}")
    }
}
