package com.samuelav.data.sourcesImpl.remote.services

import com.samuelav.data.sourcesImpl.remote.models.NetworkTodosPage
import com.samuelav.data.sourcesImpl.remote.utils.requestResult
import com.samuelav.domain.model.utils.Error
import com.samuelav.domain.model.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.url
import io.ktor.http.HttpMethod

internal class TodoService(
    private val httpClient: HttpClient,
) {
    suspend fun getTodos(): Result<Error, NetworkTodosPage> =
        httpClient.requestResult {
            method = HttpMethod.Get
            url("todos")
        }
}
