package com.samuelav.presentation.features.home.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import com.samuelav.domain.model.models.fakeTodos
import com.samuelav.domain.model.utils.Error
import com.samuelav.domain.model.utils.Result
import com.samuelav.presentation.testing.PlatformUiTest
import com.samuelav.presentation.testing.UI_TEST_TIMEOUT_MILLIS
import com.samuelav.presentation.testing.captureScreenshot
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class MainScreenScreenshotTest : PlatformUiTest() {
    @Test
    fun mainScreenLoaded() = runComposeUiTest {
        setMainScreen(FakeTodoRepository(Result.Success(fakeTodos)))
        waitUntilExactlyOneExists(hasText("Todos: ${fakeTodos.size}"), UI_TEST_TIMEOUT_MILLIS)

        captureScreenshot(onRoot(), name = "mainScreenLoaded")
    }

    @Test
    fun mainScreenLoadedDark() = runComposeUiTest {
        setMainScreen(FakeTodoRepository(Result.Success(fakeTodos)), isDarkTheme = true)
        waitUntilExactlyOneExists(hasText("Todos: ${fakeTodos.size}"), UI_TEST_TIMEOUT_MILLIS)

        captureScreenshot(onRoot(), name = "mainScreenLoadedDark")
    }

    @Test
    fun mainScreenFailure() = runComposeUiTest {
        setMainScreen(FakeTodoRepository(Result.Failure(Error.Network)))
        waitUntilExactlyOneExists(hasTestTag(MainScreenTestTags.RETRY_BUTTON), UI_TEST_TIMEOUT_MILLIS)

        captureScreenshot(onRoot(), name = "mainScreenFailure")
    }
}
