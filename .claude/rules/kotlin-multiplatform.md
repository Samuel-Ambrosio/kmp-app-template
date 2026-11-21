---
paths:
  - "**/src/*Main/**/*.kt"
  - "**/src/*Test/**/*.kt"
  - "iosApp/**/*.swift"
---

When working with Kotlin Multiplatform source sets and platform code:

## Targets and hosts

Every library module compiles for JVM, iosArm64, iosSimulatorArm64, JS and Wasm, plus Android when it applies `convention.kmp.android.library` (see `build-system.md`). The platform hosts are thin: they start the shared app and show `App()`.

| Platform | Host | Only responsibility |
| --- | --- | --- |
| Android | `androidApp/.../MainApplication.kt`, `MainActivity.kt` | `startApp { androidContext(...) }`, splash, `setContent { App() }` |
| iOS | `iosApp/iosApp/iOSApp.swift`, `ContentView.swift` + `app/src/iosMain` | `startIosApp()`, host `MainViewController()` |
| Desktop | `desktopApp/.../Main.kt` | `startApp()`, window titled `AppInfo.name` |
| Web | `webApp/src/webMain/.../Main.kt` | `startApp()`, page title, `ComposeViewport { App() }` |

**Screens, ViewModels, navigation and logic never go in a host.** Shared startup goes in `app` (`startApp`, `appComponent`); SwiftUI code in `iosApp/` is only for what has to be native.

## Put code in the most common source set that can hold it

1. `commonMain` if it compiles with common APIs and multiplatform libraries.
2. An intermediate source set when several targets share an API: the default ones (`webMain` = JS + Wasm, `iosMain`) or the custom ones a module declares (`nonWebMain` in `data:sourcesImpl:local` for Room/DataStore, `nonAndroidMain` in `presentation:analytics` and `features:advert`).
3. A leaf source set (`androidMain`, `jvmMain`, `jsMain`, `wasmJsMain`) only when the API exists for that target alone.

Prefer a multiplatform library over writing `expect`/`actual`; check what already exists before adding platform code.

## `expect`/`actual`

- Prefer, in this order: an `expect fun` returning a common type (`defaultHttpClientEngine(): HttpClientEngine`), an `expect val` Koin module (`platformLocalModule`, `platformAnalyticsModule`, see `koin-modules.md`), and only then `expect class`/`expect object` (`AppDispatchers`, `PlatformUiTest`).
- `expect` declarations are minimal: only what common code really calls (YAGNI). Every `actual` lives in the source set that matches the hierarchy, in a file named `<File>.<sourceSet>.kt` (`LocalModule.android.kt`, `AppDispatchers.web.kt`, `AdvertBanner.nonAndroid.kt`).
- An `actual` contains no business logic: it adapts a platform API, nothing else.
- Adding an `expect` means adding its `actual` for **every** target below it. Build each affected target (see "Verification" in `.claude/CLAUDE.md`): a missing `actual` only fails when that target compiles.

## Platform APIs from common code

- Never import `android.*`, `java.*`, `javax.*`, `platform.*` (Apple), `kotlinx.browser`/`org.w3c.*` in `commonMain`.
- `java.*` is not "free": it breaks iOS and web. Use `kotlin.*`, `kotlinx.*` or a multiplatform library (`kotlin.time.Clock`/`Instant` and `kotlinx-datetime` instead of `java.time`, `kotlinx-io`/Okio instead of `java.io`).
- Dispatchers: `Dispatchers.IO` does not exist on JS/Wasm. Code that needs the IO dispatcher takes it as a constructor parameter defaulting to `AppDispatchers.io` (`presentation:common`), which is `Dispatchers.IO` on JVM/Android/iOS and `Dispatchers.Default` on the web.
- Do not use `synchronized`, `@Volatile`, `java.util.concurrent` or reflection (`::class.qualifiedName`, `javaClass`) in `commonMain`; use `kotlinx.coroutines` (`Mutex`, `StateFlow`), `kotlinx-atomicfu` and `::class.simpleName`/`::class` comparisons.
- `js()` inline code is only allowed in `jsMain`/`wasmJsMain`, as the whole body of a top-level function with a constant string.

## iOS interop

- Swift only sees the public API of `app` (the `Shared` framework). Keep what Swift calls in `app/src/iosMain`, with no default arguments (Swift cannot use them: hence `startIosApp()` next to `startApp(platformDeclaration)`).
- Top-level functions are reached from Swift as `<File>Kt.<function>()` (`IosAppKt.startIosApp()`, `MainViewControllerKt.MainViewController()`); a Kotlin function starting with `init` is renamed `doInit…` in Swift: avoid that prefix in what Swift calls.
- Swift sees `suspend` functions and `Flow`s poorly. If Swift ever needs them, expose a dedicated API (callbacks or a wrapper) instead of the raw Objective-C export; ask before adding a library like SKIE or KMP-NativeCoroutines.

## Web

- `js` and `wasmJs` share `webMain`. Wasm is the primary web target; JS is the fallback for older browsers.
- Not every multiplatform library supports `wasmJs` (Room does not, for example): check its targets before adding it to `commonMain`.
- Non-UI web tests run on Node (`jsNodeTest`, `wasmJsNodeTest`); Compose UI tests need a browser (`jsBrowserTest`, `wasmJsBrowserTest`, headless Chrome).

## Tests

- Business logic is tested in `commonTest` with `kotlin.test`, so it runs on every target. Platform-specific behavior is tested in the platform test source set (`jvmTest` for `DIModulesTest`, which uses JVM-only Koin `verify`).
- Test names: backtick names may contain spaces, but not `,`, `.`, `:`, `/`, `<`, `>`, `[` or `]` (Kotlin/Native and JS reject them). UI tests use camelCase names.
- Do not write a JVM-only test for logic that lives in `commonMain`: it hides failures on other targets.
