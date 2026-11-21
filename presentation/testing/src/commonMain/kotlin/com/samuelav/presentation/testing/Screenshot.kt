package com.samuelav.presentation.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction

/**
 * Captures [node] as `src/commonTest/screenshots/<platform>/<name>.png` of the module under test when the
 * Roborazzi record/verify/compare tasks run; a plain test run does not capture. Every platform keeps
 * its own reference images: Robolectric, Skia desktop and iOS render text slightly differently.
 */
@OptIn(ExperimentalTestApi::class)
expect fun ComposeUiTest.captureScreenshot(node: SemanticsNodeInteraction, name: String)

internal fun screenshotPath(platform: String, name: String) = "src/commonTest/screenshots/$platform/$name.png"

// Simulator and driver versions shift a few anti-aliased pixels (5 of 786k seen on CI);
// a real change of a text or a component changes hundreds of them.
internal const val SCREENSHOT_CHANGE_THRESHOLD = 0.0001F
