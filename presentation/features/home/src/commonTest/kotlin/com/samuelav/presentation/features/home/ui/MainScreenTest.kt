package com.samuelav.presentation.features.home.ui

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import com.samuelav.domain.model.models.Todo
import com.samuelav.domain.model.models.fakeTodos
import com.samuelav.domain.model.utils.Error
import com.samuelav.domain.model.utils.Result
import com.samuelav.domain.repository.todo.TodoRepository
import com.samuelav.presentation.testing.PlatformUiTest
import com.samuelav.presentation.testing.UI_TEST_TIMEOUT_MILLIS
import kotlinx.coroutines.awaitCancellation
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class MainScreenTest : PlatformUiTest() {
    @Test
    fun showsAProgressIndicatorWhileLoading() = runComposeUiTest {
        setMainScreen(
            todoRepository = object : TodoRepository {
                override suspend fun getAll(): Result<Error, List<Todo>> = awaitCancellation()
            }
        )

        waitUntilExactlyOneExists(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate), UI_TEST_TIMEOUT_MILLIS)
        onNode(hasText("Todos:", substring = true)).assertDoesNotExist()
    }

    @Test
    fun showsTheNumberOfLoadedTodos() = runComposeUiTest {
        setMainScreen(todoRepository = FakeTodoRepository(Result.Success(fakeTodos)))

        waitUntilExactlyOneExists(hasText("Todos: ${fakeTodos.size}"), UI_TEST_TIMEOUT_MILLIS)
    }

    @Test
    fun showsAFullScreenErrorWithRetryInsteadOfTodosWhenLoadingFails() = runComposeUiTest {
        setMainScreen(todoRepository = FakeTodoRepository(Result.Failure(Error.Network)))

        waitUntilExactlyOneExists(hasTestTag(MainScreenTestTags.ERROR_MESSAGE), UI_TEST_TIMEOUT_MILLIS)
        onNodeWithTag(MainScreenTestTags.RETRY_BUTTON).assertExists()
        onNode(hasText("Todos:", substring = true)).assertDoesNotExist()
    }

    @Test
    fun retryLoadsTheTodosAfterAFailure() = runComposeUiTest {
        setMainScreen(todoRepository = FakeTodoRepository(Result.Failure(Error.Network), Result.Success(fakeTodos)))
        waitUntilExactlyOneExists(hasTestTag(MainScreenTestTags.RETRY_BUTTON), UI_TEST_TIMEOUT_MILLIS)

        onNodeWithTag(MainScreenTestTags.RETRY_BUTTON).performClick()

        waitUntilExactlyOneExists(hasText("Todos: ${fakeTodos.size}"), UI_TEST_TIMEOUT_MILLIS)
    }
}
