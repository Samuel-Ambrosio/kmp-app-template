package com.samuelav.data.sourcesImpl.remote.utils

import io.ktor.client.engine.HttpClientEngine

internal expect fun defaultHttpClientEngine(): HttpClientEngine
