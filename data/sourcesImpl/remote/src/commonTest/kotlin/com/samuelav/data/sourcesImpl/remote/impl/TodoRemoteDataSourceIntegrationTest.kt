package com.samuelav.data.sourcesImpl.remote.impl

import com.samuelav.data.sources.TodoRemoteDataSource
import com.samuelav.data.sourcesImpl.remote.di.remoteModule
import com.samuelav.domain.model.models.Todo
import com.samuelav.domain.model.utils.CustomAPIError
import com.samuelav.domain.model.utils.Error
import com.samuelav.domain.model.utils.Result
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TodoRemoteDataSourceIntegrationTest {
    private var respond: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
        respondJson(
            200,
            """{"todos":[]}"""
        )
    }
    private val requests = mutableListOf<HttpRequestData>()
    private val koinApplication: KoinApplication = koinApplication {
        modules(
            remoteModule,
            module {
                single<HttpClientEngine> {
                    MockEngine { request ->
                        requests += request
                        respond(request)
                    }
                }
            },
        )
    }
    private val sut: TodoRemoteDataSource = koinApplication.koin.get()

    @AfterTest
    fun tearDown() {
        koinApplication.close()
    }

    @Test
    fun requestsGetTodosAndMapsTheBodyToDomainTodos() = runTest {
        respond = {
            respondJson(
                200,
                """
                {"todos":[
                  {"id":1,"todo":"First","completed":false,"userId":1},
                  {"id":2,"todo":"Second","completed":true,"userId":1}
                ],"total":2,"skip":0,"limit":30}
                """.trimIndent(),
            )
        }

        val result = sut.getAll()

        assertEquals(
            Result.Success(
                listOf(
                    Todo(userId = 1, id = 1, title = "First", completed = false),
                    Todo(userId = 1, id = 2, title = "Second", completed = true),
                )
            ),
            result,
        )
        assertEquals(HttpMethod.Get, requests.single().method)
        assertEquals("/todos", requests.single().url.encodedPath)
    }

    @Test
    fun mapsAnEmptySuccessfulResponseToNoContent() = runTest {
        respond = { respond(content = "", status = HttpStatusCode.NoContent) }

        assertEquals(Result.Failure(Error.NoContent), sut.getAll())
    }

    @Test
    fun mapsAnErrorResponseWithApiErrorsToErrorApi() = runTest {
        respond = {
            respondJson(404, """{"errors":[{"code":404,"status":"Not Found","title":"Missing","detail":"No todos"}]}""")
        }

        assertEquals(
            Result.Failure(
                Error.Api(
                    errors = listOf(
                        CustomAPIError(code = 404, status = "Not Found", title = "Missing", detail = "No todos")
                    )
                )
            ),
            sut.getAll(),
        )
    }

    @Test
    fun mapsAnErrorResponseWithoutBodyToTheErrorOfItsStatusCode() = runTest {
        val expectedByCode = mapOf(
            400 to Error.BadRequest,
            403 to Error.Forbidden,
            404 to Error.NotFound,
            408 to Error.Timeout,
            500 to Error.Server,
            502 to Error.BadGateway,
            503 to Error.ServiceUnavailable,
            504 to Error.Server,
            401 to Error.Unknown,
        )

        expectedByCode.forEach { (code, expected) ->
            respond = { respond(content = "", status = HttpStatusCode.fromValue(code)) }

            assertEquals(Result.Failure(expected), sut.getAll(), "HTTP $code")
        }
    }

    @Test
    fun mapsAnErrorResponseWithAnEmptyErrorListToTheErrorOfItsStatusCode() = runTest {
        respond = { respondJson(503, """{"errors":[]}""") }

        assertEquals(Result.Failure(Error.ServiceUnavailable), sut.getAll())
    }

    @Test
    fun mapsAnErrorResponseWithAnUnexpectedBodyToTheErrorOfItsStatusCode() = runTest {
        respond = { respond(content = "<html>Bad request</html>", status = HttpStatusCode.BadRequest) }

        assertEquals(Result.Failure(Error.BadRequest), sut.getAll())
    }

    @Test
    fun mapsAMalformedSuccessfulBodyToJsonSyntax() = runTest {
        respond = { respondJson(200, """{"todos":[{"id":1,"userId":1}]}""") }

        assertEquals(Result.Failure(Error.JsonSyntax), sut.getAll())
    }

    @Test
    fun mapsATransportFailureToNetwork() = runTest {
        respond = { error("Connection reset") }

        assertEquals(Result.Failure(Error.Network), sut.getAll())
    }

    private fun MockRequestHandleScope.respondJson(code: Int, body: String): HttpResponseData =
        respond(
            content = body,
            status = HttpStatusCode.fromValue(code),
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
}
