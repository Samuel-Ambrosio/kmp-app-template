package com.samuelav.presentation.common.utils

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberTextSharer(): TextSharer {
    val context = LocalContext.current
    return remember(context) {
        TextSharer { title, text ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(shareIntent, title))
        }
    }
}
