package com.samuelav.presentation.features.home.ui

import androidx.compose.runtime.remember
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.samuelav.domain.repository.todo.TodoRepository
import com.samuelav.domain.usecase.todo.GetAllTodosUseCase
import com.samuelav.presentation.common.ui.theme.AppTheme
import com.samuelav.presentation.testing.WithTestViewModelStore
import kotlinx.coroutines.Dispatchers
import org.koin.compose.KoinIsolatedContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module

// An isolated Koin context per test: the global one would keep the first test's definitions alive
@OptIn(ExperimentalTestApi::class)
internal fun ComposeUiTest.setMainScreen(todoRepository: TodoRepository, isDarkTheme: Boolean = false) {
    setContent {
        val koinApplication = remember {
            koinApplication {
                modules(
                    module {
                        viewModel {
                            MainViewModel(
                                getAllTodosUseCase = GetAllTodosUseCase(todoRepository = todoRepository),
                                ioDispatcher = Dispatchers.Main,
                            )
                        }
                    }
                )
            }
        }
        KoinIsolatedContext(context = koinApplication) {
            WithTestViewModelStore {
                AppTheme(isDarkTheme = isDarkTheme) { MainScreen() }
            }
        }
    }
}
