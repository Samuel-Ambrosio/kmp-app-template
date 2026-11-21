package com.samuelav.presentation.common.ui.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// The browser has a single thread and no blocking IO: Dispatchers.IO does not exist on JS/Wasm
actual object AppDispatchers {
    actual val io: CoroutineDispatcher = Dispatchers.Default
}
