package com.samuelav.presentation.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import platform.UIKit.UIActivityViewController

@Composable
internal actual fun rememberTextSharer(): TextSharer {
    val viewController = LocalUIViewController.current
    return remember(viewController) {
        TextSharer { _, text ->
            val activityViewController = UIActivityViewController(
                activityItems = listOf(text),
                applicationActivities = null
            )
            viewController.presentViewController(activityViewController, animated = true, completion = null)
        }
    }
}
