---
paths:
  - "presentation/features/**/*.kt"
---

When working in feature modules (`presentation:features:*`):

## Structure of a feature module

- `build.gradle.kts` with `id("convention.kmp.feature")` (it already brings `domain:model`, `domain:useCase`, `presentation:analytics` and `presentation:common`, and `domain:repository` + `presentation:testing` for tests) and its `namespace` in `Namespaces.Presentation.Features` (`build-logic/.../Namespaces.kt`), set in `kotlin { android { namespace = … } }`.
- Everything in `src/commonMain`. Platform source sets only for an `actual` the feature cannot avoid (`features:advert`).
- `di/Feature{X}Module.kt` with `val feature{X}Module = module { viewModel { ... } }`, added to `appComponent` (`app/src/commonMain/.../di/AppComponent.kt`).
- `ui/`: screens, ViewModels and `{X}Navigation.kt` with the `EntryProviderScope<NavKey>.{x}NavEntries(...)` extension that registers its destinations (see `navigation.md`).
- The module is registered in `settings.gradle.kts` and added to `commonMain` of `app/build.gradle.kts`.

## `Result<F, S>` at the UseCase boundary — not to be confused with the presentation `State`

`domain/model/.../utils/Result.kt` defines the wrapper returned by repositories and UseCases:

```kotlin
sealed class Result<out F, out S> {
    data object Loading : Result<Nothing, Nothing>()
    data class Success<S>(val data: S) : Result<Nothing, S>()
    data class Failure<F>(val error: F) : Result<F, Nothing>()
}
```

with helpers (`fold`, `mapSuccess`, `ifSuccess`, `successOrNull`, `failureOrNull`, `asSuccess`, `asFailure`). **There is no shared generic `UiState`**: each ViewModel collapses the `Result` it receives into its own `State`. There are two valid shapes; pick based on the case:

**1. `data class` with `isLoading`/`error`**: when loading/error/data coexist as flags over the same shape:

```kotlin
internal data class MainState(
    val isLoading: Boolean = false,
    val error: Error? = null,
    val todos: List<Todo> = emptyList(),
)
```

**2. `sealed interface` with `Loading`/`Ready`/`Failure`**: when each case needs its own, mutually exclusive data:

```kotlin
internal sealed interface DetailState {
    data object Loading : DetailState
    data class Ready(val item: Item) : DetailState
    data class Failure(val error: Error) : DetailState
}
```

Do not introduce a shared `UiState` that tries to unify both shapes without a real consumer justifying it (premature abstraction).

## Mandatory loading/success/error handling

Every screen that consumes a loading `State` **MUST** handle all 3 cases: never leave the error silent or the loading without feedback.

**Full-screen retry** (when the screen's main load fails): a full-screen error state with the message (`error.handleErrorMessage()`) and a retry button (`AppButton`) whose `onClick` points to a real ViewModel method that invokes the UseCase again (`viewModel::retry`), never to an empty callback. If it repeats across screens, extract it into a component in `presentation:common` instead of duplicating it.

With the `sealed interface` shape (the usual one with `ColdStateBaseViewModel`, where `retry` is `retryInitState()`; see `viewmodel.md`), the equivalent is an exhaustive `when` over the `State`, one composable per case:

```kotlin
when (state) {
    is DetailState.Failure -> ErrorContent(error = state.error, onRetryClick = viewModel::retry)
    is DetailState.Loading -> LoadingContent()
    is DetailState.Ready -> DetailReadyContent(state = state, /* ... */)
}
```

**Snackbar** (when a one-off action on already-loaded content fails: delete, save; not the initial load): triggered from a ViewModel `Command` and shown with the app's `SnackbarHostState`. **It is not injected through Koin**: it is a single instance in `AppState.snackbarHostState` (`rememberAppState()`), passed down as a parameter from `*NavEntries` and received in the screen as `appSnackbarHostState`:

```kotlin
CommandHandler(viewModel) { command ->
    when (command) {
        is DetailCommand.DeleteFailed ->
            appSnackbarHostState.showSnackbar(message = errorMessage(command.error))
    }
}
```

If a `Command` triggers navigation and also a snackbar that must outlive the screen that launched it, launch it in `AppState.appScope` (app lifetime), not in a scope of the screen itself, which may disappear halfway.

**How to choose**: if the failure prevents showing the main content → full-screen retry (`state.error`/`Failure`). If the content is already loaded and a secondary action fails → snackbar (`Command`). Do not mix both for the same kind of failure.

(`handleErrorMessage()` is `@Composable`: inside a `CommandHandler`, resolve the text with `getString(Res.string.…)`, which is `suspend`, or keep the error in state and show the snackbar from a `LaunchedEffect`.)

**Reference implementation**: `MainScreen`/`MainViewModel` (`presentation:features:home`) follow the `sealed interface` shape with `ColdStateBaseViewModel`: `Loading` → progress indicator, `Ready` → content, `Failure` → full-screen error with a retry `AppButton` bound to `viewModel::retry`. `MainScreenTest` covers the three cases and the retry, and `MainScreenScreenshotTest` records them.

## Screen and screenshot tests — `commonTest`, every platform

Screen tests live in `src/commonTest` and run on Android (Robolectric), desktop, iOS and the browser:

- The test class extends `PlatformUiTest` (`presentation:testing`): on Android it runs the test with Robolectric; elsewhere it fixes the locale so screenshots do not depend on the machine.
- `runComposeUiTest { … }` with the screen inside `KoinIsolatedContext` (a fresh `koinApplication` per test with the fake definitions) and `WithTestViewModelStore`, as `setMainScreen` does. **Never the `KoinApplication` composable or `startKoin`**: the global context survives between tests and the next test gets the previous test's ViewModel.
- The test class replaces `Dispatchers.Main` with an `UnconfinedTestDispatcher` (`setMain` in `@BeforeTest`, `resetMain` in `@AfterTest`) and the ViewModel gets `ioDispatcher = Dispatchers.Main`: in the browser the real Main dispatcher only runs between event-loop turns, and `waitUntil` never yields one, so the screen would stay in its first state.
- Compose UI tests do not run on the JS browser target (Skiko is not initialized under Karma): `convention.kmp.feature` disables `jsBrowserTest`. The web UI tests run on Wasm.
- Find nodes by test tag (`MainScreenTestTags`) or non-localized text, not by a translated string: the locale differs between platforms.
- Wait with `waitUntilExactlyOneExists(matcher, UI_TEST_TIMEOUT_MILLIS)`: resources load asynchronously and the default 1 s is not always enough.
- Screenshots: `captureScreenshot(onRoot(), name = "…")`. Each platform keeps its own reference images in `src/commonTest/screenshots/<android|desktop|ios>/` (they render text differently); the web captures nothing. A plain test run does not capture; `recordRoborazzi*` records and `verifyRoborazzi*` compares.
