package com.samuelav.data.sourcesImpl.remote.utils

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

internal actual fun defaultHttpClientEngine(): HttpClientEngine = Js.create()
