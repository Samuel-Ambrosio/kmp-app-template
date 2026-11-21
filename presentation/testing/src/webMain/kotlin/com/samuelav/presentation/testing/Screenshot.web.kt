package com.samuelav.presentation.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction

@OptIn(ExperimentalTestApi::class)
@Suppress("UnusedParameter")
actual fun ComposeUiTest.captureScreenshot(node: SemanticsNodeInteraction, name: String) = Unit
