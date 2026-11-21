package com.samuelav.data.sourcesImpl.remote.models

import com.samuelav.data.sourcesImpl.remote.di.remoteModule
import com.samuelav.domain.model.utils.CustomAPIError
import com.samuelav.domain.model.utils.Error
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NetworkModelsDecodingTest {
    private val koinApplication: KoinApplication = koinApplication { modules(remoteModule) }
    private val json: Json = koinApplication.koin.get()

    @AfterTest
    fun tearDown() {
        koinApplication.close()
    }

    @Test
    fun decodesATodoAndMapsItToTheDomainModel() {
        val networkTodo = json.decodeFromString<NetworkTodo>(
            """{"id":7,"todo":"Buy milk","completed":true,"userId":3}"""
        )

        assertEquals(NetworkTodo(userId = 3, id = 7, title = "Buy milk", completed = true), networkTodo)
        assertEquals(7, networkTodo.toTodo().id)
        assertEquals("Buy milk", networkTodo.toTodo().title)
    }

    @Test
    fun failsToDecodeATodoWithoutARequiredField() {
        assertFailsWith<SerializationException> {
            json.decodeFromString<NetworkTodo>("""{"id":7,"completed":true,"userId":3}""")
        }
    }

    @Test
    fun decodesATodosPageIgnoringThePaginationFields() {
        val page = json.decodeFromString<NetworkTodosPage>(
            """{"todos":[{"id":1,"todo":"First","completed":false,"userId":2}],"total":1,"skip":0,"limit":30}"""
        )

        assertEquals(
            NetworkTodosPage(todos = listOf(NetworkTodo(userId = 2, id = 1, title = "First", completed = false))),
            page
        )
    }

    @Test
    fun decodesAnApiErrorAndMapsItToErrorApi() {
        val json = """{"errors":[{"code":404,"status":"Not Found","title":"Missing","detail":"No todo"}]}"""

        val error = this.json.decodeFromString<NetworkApiError>(json).toError()

        assertEquals(
            Error.Api(
                errors = listOf(CustomAPIError(code = 404, status = "Not Found", title = "Missing", detail = "No todo"))
            ),
            error,
        )
    }

    @Test
    fun decodesAnApiErrorWithoutErrors() {
        assertEquals(Error.Api(errors = null), json.decodeFromString<NetworkApiError>("{}").toError())
    }
}
