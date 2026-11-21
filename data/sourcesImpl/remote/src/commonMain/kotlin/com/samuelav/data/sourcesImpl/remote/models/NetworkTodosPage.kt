package com.samuelav.data.sourcesImpl.remote.models

import kotlinx.serialization.Serializable

@Serializable
internal data class NetworkTodosPage(
    val todos: List<NetworkTodo>,
)
