---
paths:
  - "build-logic/**"
  - "gradle/libs.versions.toml"
  - "settings.gradle.kts"
  - "build.gradle.kts"
  - "**/build.gradle.kts"
  - "gradle.properties"
  - "gradle/gradle-daemon-jvm.properties"
  - "**/proguard-rules.pro"
  - "iosApp/Configuration/*.xcconfig"
  - "iosApp/iosApp.xcodeproj/project.pbxproj"
---

## Library versions — `gradle/libs.versions.toml`, never hardcoded

All versions (dependencies, plugins and SDK: `android-compileSdk`, `android-targetSdk`, `android-minSdk`, `jvmTarget`) are declared in `[versions]` of `gradle/libs.versions.toml` and referenced through `libs.xxx` in `build.gradle.kts` files, or through `libs.library("…")`/`libs.intVersion("…")` (`build-logic/.../VersionCatalog.kt`) in convention plugins. Never a literal version in a `build.gradle.kts`.

Plugins are declared once in the root `build.gradle.kts` with `apply false` (same classloader for all projects); `build-logic` only compiles against them (`compileOnly`).

- In `commonMain` use the multiplatform artifacts (`org.jetbrains.compose.*`, `org.jetbrains.androidx.lifecycle`, `org.jetbrains.androidx.navigation3`), never the Android-only `androidx.compose.*` ones. The only `androidx.compose` artifacts in the catalog are the Android test ones (`ui-test-junit4`, `ui-test-manifest`): keep their version equal to the AndroidX Compose version that Compose Multiplatform resolves (`./gradlew :presentation:common:dependencyInsight --configuration androidCompileClasspath --dependency androidx.compose.ui:ui`), or they drag a newer Compose that requires another compileSdk.
- Keep `composeMultiplatform`, `kotlin`, `material3`, `lifecycle` and `navigation3` compatible with each other: check the Compose Multiplatform release notes when bumping any of them.
- **Before adding a dependency to `commonMain`, check it publishes artifacts for every target of the module** (Android, iosArm64, iosSimulatorArm64, JVM, JS, Wasm). If it does not, it goes in the source set of the targets it supports (`nonWebMain`, `androidMain`…), behind a common contract.

To look for updates: `./gradlew dependencyUpdates --no-parallel`.

## Convention plugins (`build-logic/src/main/kotlin/`)

Chain of precompiled plugins (`plugins { id("convention.…") }`):

```
convention.detekt                     (detekt + ktlint over every source set, config/detekt/detekt.yml)
   ▲
   ├── convention.kmp.library          (Kotlin Multiplatform without Android: jvm, iosArm64, iosSimulatorArm64,
   │      ▲                             js and wasmJs (browser + Node, tests on Node); jvmToolchain;
   │      │                             coroutines, serialization, Koin; tests: kotlin-test, coroutines-test, Turbine)
   │      └── convention.kmp.android.library   (+ Android target: compileSdk/minSdk, host tests with resources)
   │             ▲
   │             └── convention.kmp.compose.library   (+ Compose Multiplatform, resources, Material 3, lifecycle,
   │                    ▲                               Navigation 3, Koin Compose; tests: Compose UI test;
   │                    │                               web tests in Karma + ChromeHeadless)
   │                    └── convention.kmp.feature    (+ domain:model, domain:useCase, presentation:analytics,
   │                                                    presentation:common; tests: domain:repository,
   │                                                    presentation:testing; Roborazzi; Robolectric native graphics)
   └── convention.android.application  (androidApp: identity from gradle.properties, targetSdk,
                                         Compose, desugaring, R8 in release)

convention.kmp.environment            (BuildKonfig dev/pre/pro: ENVIRONMENT, API_BASE_PATH, HTTP_LOGGING)
```

- When creating a new module, apply the most specific plugin possible (`convention.kmp.feature` for a feature, not bare `convention.kmp.compose.library`). A module that does not need Android APIs or Android resources uses `convention.kmp.library`: Android consumes its JVM variant.
- Android namespaces go in `Namespaces.kt` (`build-logic/.../Namespaces.kt`) and are set in `kotlin { android { namespace = … } }`, not as a string in the module.
- Inter-module dependencies use type-safe accessors (`implementation(projects.domain.model)`), not `project(":…")`, except inside `build-logic`, where they do not exist.
- A new module is registered in `include(...)` of `settings.gradle.kts`.
- Configuration that only one module needs goes in its `build.gradle.kts`, not in a convention plugin. A custom intermediate source set (`nonWebMain`, `nonAndroidMain`) is declared in the module with `applyDefaultHierarchyTemplate { common { group(…) } }`; when it must include `iosMain`, use `group("ios") { withIos() }` so the intermediate `iosMain` sees it too.

## App identity — `gradle.properties`, the only place

`app.name`, `app.applicationId`, `app.versionName` and `app.versionCode` are read through `Project.appIdentity` (`build-logic/.../AppIdentity.kt`) and reach every platform:

| Platform | Where it is applied |
| --- | --- |
| Android | `convention.android.application` (`applicationId`, `versionCode`/`versionName`, `@string/app_name` as `resValue`) |
| iOS | `:app:syncAppIdentity` writes `iosApp/Configuration/Identity.xcconfig` (included by `Config.xcconfig`; `CFBundleDisplayName = $(APP_DISPLAY_NAME)`). It runs before `embedAndSignAppleFrameworkForXcode` |
| Desktop | `desktopApp/build.gradle.kts` (`packageName`, `packageVersion`, macOS `bundleID`); window title from `AppInfo.name` |
| Web | page title from `AppInfo.name` |
| Gradle | `rootProject.name` in `settings.gradle.kts` (name without whitespace) |

Never hardcode the app name, id or version anywhere else (Kotlin, Swift, Xcode build settings, `index.html`). `Identity.xcconfig` is generated: do not edit it by hand. `Config.xcconfig` only keeps `TEAM_ID`.

## Environments and `BuildKonfig`

`convention.kmp.environment` is the multiplatform replacement of Android product flavors (Android flavors do not reach shared code, iOS, desktop or web). It defines the `dev`/`pre`/`pro` BuildKonfig flavors; the one compiled in is the `buildkonfig.flavor` Gradle property (`dev` in `gradle.properties`, overridable with `-Pbuildkonfig.flavor=pro`). Only `data:sourcesImpl:remote` applies it: do not apply it to a module that does not read a per-environment value. A module that needs a non-environment build value (`app`: `APP_NAME`; `data:sourcesImpl:local`: `APPLICATION_ID`) applies the BuildKonfig plugin directly with `defaultConfigs`.

Never put a value that changes between builds (date, hash, counter) in a BuildKonfig field: it changes the generated source and forces recompiling every dependent module.

## Proguard/R8

`androidApp` release minifies with R8 (`isMinifyEnabled = true` in `convention.android.application`). Ktor, kotlinx.serialization, Koin, Room and Compose ship their own consumer rules: do not copy them. A rule for our code or a third-party library whose rules are not enough goes in `androidApp/proguard-rules.pro`. A green `assembleRelease` does not prove the app starts: launch the minified release on an emulator (see `engineering-principles.md`).

## Repositories — only in `settings.gradle.kts`

All repositories are declared in `dependencyResolutionManagement` of `settings.gradle.kts`, with `RepositoriesMode.FAIL_ON_PROJECT_REPOS`: a `repositories { }` block in a `build.gradle.kts` breaks the build. `google()` is only queried for `androidx.*`, `com.android.*` and `com.google.*`. The Node.js, Yarn and Binaryen distributions that the Kotlin/JS and Kotlin/Wasm plugins download are declared there as `exclusiveContent` Ivy repositories, and the root `build.gradle.kts` unsets the plugins' own `downloadBaseUrl`. A new third-party repository goes in an `exclusiveContent` block with the groups it serves (dependency confusion).

## iOS framework

`app` is the only module exported to iOS, as the static `Shared` framework (`binaries.framework` in `app/build.gradle.kts`), built by the Xcode build phase `./gradlew :app:embedAndSignAppleFrameworkForXcode`. Keep its public API small: what Swift calls lives in `app/src/iosMain` (`MainViewController`, `startIosApp`). The iOS deployment target in the Xcode project must not be lower than the one the Kotlin/Native libraries are built for (the linker warns about it).

## JVM — JDK 25

- The JDK that runs Gradle is pinned by `gradle/gradle-daemon-jvm.properties` for the IDE, the terminal and CI, over `JAVA_HOME`. Regenerate it with `./gradlew updateDaemonJvm --jvm-version=N` (that is why `settings.gradle.kts` applies `foojay-resolver-convention`).
- The toolchain and bytecode target come from `jvmTarget` in `libs.versions.toml`, applied in `convention.kmp.library`, `convention.android.application` and `desktopApp`.
- When upgrading the JDK, change `jvmTarget`, `gradle-daemon-jvm.properties` and the `java-version` entries in `.github/workflows/ci.yml` together.

## Secrets

**NEVER** hardcode keys or secrets in Kotlin, Gradle files or `.xcconfig`: they go in `local.properties` (gitignored) or CI secrets and reach the code through BuildKonfig.
