package com.samuelav.presentation.common.extensions

import androidx.compose.runtime.Composable
import com.samuelav.domain.model.utils.Error
import com.samuelav.presentation.common.resources.Res
import com.samuelav.presentation.common.resources.result_error_bad_gateway
import com.samuelav.presentation.common.resources.result_error_bad_request
import com.samuelav.presentation.common.resources.result_error_forbidden
import com.samuelav.presentation.common.resources.result_error_network
import com.samuelav.presentation.common.resources.result_error_no_content
import com.samuelav.presentation.common.resources.result_error_not_found
import com.samuelav.presentation.common.resources.result_error_server
import com.samuelav.presentation.common.resources.result_error_service_unavailable
import com.samuelav.presentation.common.resources.result_error_timeout
import com.samuelav.presentation.common.resources.result_error_unknown
import org.jetbrains.compose.resources.stringResource

@Composable
fun Error.handleErrorMessage(): String =
    when (this) {
        is Error.NoContent -> stringResource(Res.string.result_error_no_content)
        is Error.BadRequest -> stringResource(Res.string.result_error_bad_request)
        is Error.Forbidden -> stringResource(Res.string.result_error_forbidden)
        is Error.NotFound -> stringResource(Res.string.result_error_not_found)
        is Error.Timeout -> stringResource(Res.string.result_error_timeout)
        is Error.Server -> stringResource(Res.string.result_error_server)
        is Error.BadGateway -> stringResource(Res.string.result_error_bad_gateway)
        is Error.ServiceUnavailable -> stringResource(Res.string.result_error_service_unavailable)
        is Error.JsonSyntax -> stringResource(Res.string.result_error_unknown)
        is Error.Network -> stringResource(Res.string.result_error_network)
        is Error.Unknown -> stringResource(Res.string.result_error_unknown)
        is Error.Api -> title ?: stringResource(Res.string.result_error_unknown)
    }
