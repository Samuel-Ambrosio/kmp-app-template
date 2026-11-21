package com.samuelav.app

import androidx.compose.ui.window.ComposeUIViewController
import com.samuelav.app.ui.App
import platform.UIKit.UIViewController

@Suppress("FunctionNaming")
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
