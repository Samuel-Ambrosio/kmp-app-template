package com.samuelav.presentation.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal actual fun rememberTextSharer(): TextSharer = remember { TextSharer(::shareOrCopy) }

@Suppress("UnusedParameter")
private fun shareOrCopy(title: String, text: String): Unit =
    js(
        "{ if (navigator.share) { navigator.share({ title: title, text: text }).catch(function () {}); }" +
            " else { navigator.clipboard.writeText(text); } }"
    )
