package com.samuelav.presentation.common.ui.base

import kotlinx.coroutines.CoroutineDispatcher

expect object AppDispatchers {
    val io: CoroutineDispatcher
}
