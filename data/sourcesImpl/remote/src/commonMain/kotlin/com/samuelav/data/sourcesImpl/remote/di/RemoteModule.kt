package com.samuelav.data.sourcesImpl.remote.di

import com.samuelav.data.sources.TodoRemoteDataSource
import com.samuelav.data.sourcesImpl.remote.BuildKonfig
import com.samuelav.data.sourcesImpl.remote.impl.TodoRemoteDataSourceImpl
import com.samuelav.data.sourcesImpl.remote.services.TodoService
import com.samuelav.data.sourcesImpl.remote.utils.defaultHttpClientEngine
import com.samuelav.domain.model.utils.debug.AppLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

private const val REQUEST_TIMEOUT_MILLIS = 30_000L

public val remoteModule: Module = module {
    single<HttpClientEngine> { defaultHttpClientEngine() }
    single { jsonProvider() }
    single { httpClientProvider(engine = get(), json = get()) }

    single { TodoService(httpClient = get()) }

    single<TodoRemoteDataSource> { TodoRemoteDataSourceImpl(todoService = get()) }
}

/**
 *  Ktor configuration
 */
private fun jsonProvider(): Json =
    Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

private fun httpClientProvider(engine: HttpClientEngine, json: Json): HttpClient =
    HttpClient(engine) {
        expectSuccess = false
        defaultRequest { url(BuildKonfig.API_BASE_PATH) }
        install(HttpTimeout) { requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS }
        install(ContentNegotiation) { json(json) }
        if (BuildKonfig.HTTP_LOGGING) {
            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) = AppLogger.d(message)
                }
            }
        }
    }
