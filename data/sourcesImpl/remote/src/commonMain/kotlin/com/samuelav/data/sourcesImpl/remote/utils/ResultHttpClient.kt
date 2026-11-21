package com.samuelav.data.sourcesImpl.remote.utils

import com.samuelav.data.sourcesImpl.remote.models.NetworkApiError
import com.samuelav.data.sourcesImpl.remote.models.toError
import com.samuelav.domain.model.utils.Error
import com.samuelav.domain.model.utils.Result
import com.samuelav.domain.model.utils.debug.AppLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

internal suspend inline fun <reified S : Any> HttpClient.requestResult(
    crossinline block: HttpRequestBuilder.() -> Unit,
): Result<Error, S> =
    try {
        val response = request { block() }
        when {
            !response.status.isSuccess() -> Result.Failure(response.toApiError())
            response.isEmpty() -> Result.Failure(Error.NoContent)
            else -> Result.Success(response.body<S>())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
        val error = e.toError()
        AppLogger.e(e, "Error: $error")
        Result.Failure(error)
    }

@PublishedApi
internal suspend fun HttpResponse.isEmpty(): Boolean =
    status == HttpStatusCode.NoContent || bodyAsText().isBlank()

@PublishedApi
internal suspend fun HttpResponse.toApiError(): Error {
    val apiError =
        try {
            body<NetworkApiError>().toError()
        } catch (e: CancellationException) {
            throw e
        } catch (@Suppress("TooGenericExceptionCaught", "SwallowedException") _: Exception) {
            null
        }
    return if (apiError?.errors.isNullOrEmpty()) status.value.toHttpError() else apiError
}

@PublishedApi
internal fun Throwable.toError(): Error =
    when {
        hasCause<SerializationException>() -> Error.JsonSyntax
        this is HttpRequestTimeoutException || this is SocketTimeoutException -> Error.Timeout
        else -> Error.Network
    }

private inline fun <reified T : Throwable> Throwable.hasCause(): Boolean =
    generateSequence(this) { it.cause }.any { it is T }

private fun Int.toHttpError(): Error =
    when (this) {
        400 -> Error.BadRequest
        403 -> Error.Forbidden
        404 -> Error.NotFound
        408 -> Error.Timeout
        502 -> Error.BadGateway
        503 -> Error.ServiceUnavailable
        in 500..599 -> Error.Server
        else -> Error.Unknown
    }
