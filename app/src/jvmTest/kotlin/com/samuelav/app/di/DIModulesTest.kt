package com.samuelav.app.di

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify
import kotlin.test.Test

class DIModulesTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun appComponentResolvesEveryDefinition() {
        module { includes(appComponent) }.verify(
            extraTypes = listOf(
                // Built with their own defaults, not resolved from the graph
                CoroutineDispatcher::class,
                HttpClientConfig::class,
                HttpClientEngine::class,
            ),
        )
    }
}
