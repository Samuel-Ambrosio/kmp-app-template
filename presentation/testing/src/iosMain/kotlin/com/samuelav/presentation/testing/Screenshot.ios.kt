package com.samuelav.presentation.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.InternalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.roborazziSystemPropertyProjectPath
import io.github.takahirom.roborazzi.captureRoboImage

// On iOS a relative path lands in build/outputs/roborazzi: an absolute one keeps the images next to the other platforms'
@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class, InternalRoborazziApi::class)
actual fun ComposeUiTest.captureScreenshot(node: SemanticsNodeInteraction, name: String) {
    node.captureRoboImage(
        composeUiTest = this,
        filePath = "${roborazziSystemPropertyProjectPath()}/${screenshotPath(platform = "ios", name = name)}",
        roborazziOptions = RoborazziOptions(
            compareOptions = RoborazziOptions.CompareOptions(changeThreshold = SCREENSHOT_CHANGE_THRESHOLD),
        ),
    )
}
