package com.samuelav.kmpapptemplate

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "kmp-app-template",
    ) {
        App()
    }
}