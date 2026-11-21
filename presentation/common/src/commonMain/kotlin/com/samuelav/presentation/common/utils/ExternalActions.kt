package com.samuelav.presentation.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import com.samuelav.domain.model.utils.debug.AppLogger
import com.samuelav.presentation.common.resources.Res
import com.samuelav.presentation.common.resources.app_email_contact
import org.jetbrains.compose.resources.stringResource

class ExternalActions internal constructor(
    private val uriHandler: UriHandler,
    private val defaultEmailRecipient: String,
    private val textSharer: TextSharer,
) {
    fun sendEmail(
        to: String = defaultEmailRecipient,
        subject: String,
        onFailedSendEmail: () -> Unit,
    ) {
        try {
            uriHandler.openUri("mailto:$to?subject=${subject.encodeUriComponent()}")
        } catch (@Suppress("TooGenericExceptionCaught") ex: Exception) {
            AppLogger.e(ex, "sendEmail failed")
            onFailedSendEmail()
        }
    }

    fun shareText(title: String, sharedText: String) {
        textSharer.share(title = title, text = sharedText)
    }
}

@Composable
fun rememberExternalActions(): ExternalActions {
    val uriHandler = LocalUriHandler.current
    val defaultEmailRecipient = stringResource(Res.string.app_email_contact)
    val textSharer = rememberTextSharer()
    return remember(uriHandler, defaultEmailRecipient, textSharer) {
        ExternalActions(
            uriHandler = uriHandler,
            defaultEmailRecipient = defaultEmailRecipient,
            textSharer = textSharer,
        )
    }
}

internal fun interface TextSharer {
    fun share(title: String, text: String)
}

// Android share sheet, iOS activity sheet, Web Share API; desktop has no share sheet and copies the text instead
@Composable
internal expect fun rememberTextSharer(): TextSharer

private const val URI_UNRESERVED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~"

private fun String.encodeUriComponent(): String =
    encodeToByteArray().joinToString(separator = "") { byte ->
        val char = byte.toInt().toChar()
        if (char in URI_UNRESERVED_CHARS) {
            char.toString()
        } else {
            "%" + byte.toUByte().toString(radix = 16).uppercase().padStart(2, '0')
        }
    }
