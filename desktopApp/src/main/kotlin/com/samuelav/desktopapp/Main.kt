package com.samuelav.desktopapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.samuelav.app.AppInfo
import com.samuelav.app.startApp
import com.samuelav.app.ui.App

fun main() {
    startApp()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = AppInfo.name,
        ) {
            App()
        }
    }
}
