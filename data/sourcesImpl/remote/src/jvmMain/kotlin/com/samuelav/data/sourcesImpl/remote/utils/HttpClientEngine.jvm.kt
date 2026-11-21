package com.samuelav.data.sourcesImpl.remote.utils

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun defaultHttpClientEngine(): HttpClientEngine = OkHttp.create()
