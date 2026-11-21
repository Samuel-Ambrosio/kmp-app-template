package com.samuelav.webapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.samuelav.app.AppInfo
import com.samuelav.app.startApp
import com.samuelav.app.ui.App
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startApp()
    document.title = AppInfo.name

    ComposeViewport {
        App()
    }
}
