package com.samuelav.presentation.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage

@OptIn(ExperimentalTestApi::class)
actual fun ComposeUiTest.captureScreenshot(node: SemanticsNodeInteraction, name: String) {
    node.captureRoboImage(
        filePath = screenshotPath(platform = "android", name = name),
        roborazziOptions = screenshotOptions
    )
}

private val screenshotOptions =
    RoborazziOptions(compareOptions = RoborazziOptions.CompareOptions(changeThreshold = SCREENSHOT_CHANGE_THRESHOLD))
