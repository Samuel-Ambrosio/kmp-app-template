<div align="center">

# 📱 Kotlin Multiplatform Template

**A modular, ready-to-ship starting point for Android, iOS, desktop and web apps built with Kotlin Multiplatform and Compose Multiplatform.**

[![CI](https://github.com/Samuel-Ambrosio/kmp-app-template/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Samuel-Ambrosio/kmp-app-template/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.12-4285F4?logo=jetpackcompose&logoColor=white)
![Platforms](https://img.shields.io/badge/platforms-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web-informational)
![JDK](https://img.shields.io/badge/JDK-25-ED8B00?logo=openjdk&logoColor=white)

[Features](#-features) •
[Tech stack](#-tech-stack) •
[Getting started](#-creating-a-new-app-from-this-template) •
[Project structure](#-project-structure) •
[Tooling](#-tooling) •
[License](#-license)

</div>

---

## ✨ Features

- 🌍 **One codebase, four platforms** — Android, iOS, desktop (JVM) and web (Wasm + JS) share the UI, the business logic and the data layer.
- 🧱 **Clean, multi-module architecture** — `domain`, `data` and `presentation` layers split into focused Kotlin Multiplatform modules.
- 🔌 **Convention plugins** in a `build-logic` included build — no duplicated build scripts.
- 🏷️ **Configurable app identity** — name, id and version in one place (`gradle.properties`) for every platform.
- 🌐 **Backend environments** `dev` / `pre` / `pro`, each with its own API base path, available to shared code.
- 🎨 **Compose Multiplatform + Material 3**, Navigation 3 and an app theme out of the box.
- 🔥 **Firebase Analytics & Crashlytics** and **AdMob** wired in on Android, ready to switch on.
- 💾 **Room** and **DataStore** prepared for local storage.
- 🧪 **Shared tests** — unit, integration, screen and screenshot tests written once in `commonTest`.
- 🔍 **Detekt + ktlint** static analysis and a **GitHub Actions** CI pipeline.

## 🛠 Tech stack

| Area | Libraries |
| --- | --- |
| Language | Kotlin Multiplatform, Coroutines, kotlinx.serialization, kotlinx-datetime |
| UI | Compose Multiplatform, Material 3, Navigation 3, Compose resources |
| DI | Koin |
| Networking | Ktor (OkHttp / Darwin / Fetch engines) |
| Storage | Room, DataStore (Android, iOS, desktop) |
| Logging | Kermit, behind `AppLogger` |
| Firebase (Android) | Analytics, Crashlytics |
| Ads (Android) | Google Mobile Ads (AdMob) |
| Configuration | BuildKonfig |
| Quality | Detekt, ktlint, kotlin-test, Turbine, Compose UI test, Robolectric, Roborazzi |

All versions are managed in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

### What runs where

| | Android | iOS | Desktop | Web |
| --- | :---: | :---: | :---: | :---: |
| Shared UI, navigation, DI, networking | ✅ | ✅ | ✅ | ✅ |
| Room / DataStore | ✅ | ✅ | ✅ | ❌ (in-memory) |
| Firebase Analytics / Crashlytics | ✅ | ⚙️ native SDK | ❌ (logged) | ❌ (logged) |
| AdMob banner | ✅ | ⚙️ native SDK | ❌ | ❌ |
| Screenshot tests (Roborazzi) | ✅ | ✅ experimental | ✅ | ❌ |

⚙️ Not wired in: the iOS SDKs have to be added from Xcode (see steps 4 and 5).

---

## 🚀 Creating a new app from this template

Work through this list top to bottom. Everything marked *optional* can stay as it is.

### 1️⃣ Identity <sup>`required`</sup>

- [ ] `gradle.properties` → `app.name` (display name on every platform; the Gradle root project and the iOS `PRODUCT_NAME` use it without spaces), `app.applicationId` (Android application id, iOS bundle id, macOS bundle id), `app.versionName` and `app.versionCode`.
- [ ] Run `./gradlew syncAppIdentity` once so Xcode picks up the new values (it writes `iosApp/Configuration/Identity.xcconfig`; Xcode builds regenerate it afterwards).
- [ ] `iosApp/Configuration/Config.xcconfig` → `TEAM_ID` (your Apple development team).
- [ ] *Optional* — package prefix `com.samuelav`: it is the author's namespace, not the app's, so it does not need to change. To change it, rename the source folders of every module, `build-logic/.../Namespaces.kt`, the `com.samuelav.buildlogic` package and the `packageOfResClass` of `presentation/common`.

### 2️⃣ Branding <sup>`required`</sup>

- [ ] Android launcher icon: `androidApp/src/main/res/mipmap-*/ic_launcher*` and `drawable(-night)/ic_launcher_{foreground,background}.xml` (Android Studio → *New → Image Asset*). The foreground drawable is also the splash icon (`androidApp/src/main/res/values(-night)/themes.xml`).
- [ ] iOS app icon: `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset`.
- [ ] Colors, typography and spacing: `presentation/common/src/commonMain/kotlin/.../ui/theme/*.kt`; fonts and icons in `presentation/common/src/commonMain/composeResources/`.
- [ ] Strings in `presentation/common/src/commonMain/composeResources/values/strings.xml`: `app_email_contact` (default recipient of `ExternalActions.sendEmail`), `app_share_link` (store link to use with `shareText`), and `developed_by` (also in `values-es/`).

### 3️⃣ Backend <sup>`required`</sup>

- [ ] `API_BASE_PATH` per environment (`dev` / `pre` / `pro`) in `build-logic/src/main/kotlin/convention.kmp.environment.gradle.kts`. The environment compiled in is `buildkonfig.flavor` in `gradle.properties` (`dev` by default); override it per build with `-Pbuildkonfig.flavor=pro`.
- [ ] Replace the sample *Todo* feature ([DummyJSON](https://dummyjson.com/docs/todos)): `TodoService`, `NetworkTodo`, `NetworkTodosPage`, `TodoRemoteDataSource(Impl)`, `TodoRepository(Impl)`, `GetAllTodosUseCase`, `Todo` and the home screen that uses it.

### 4️⃣ Firebase — Analytics + Crashlytics <sup>`required before release`</sup>

> [!WARNING]
> The Firebase Android SDKs are already dependencies; until this step is done, Analytics/Crashlytics calls will fail at runtime on Android.

- [ ] Create the Firebase project, register `app.applicationId` and copy `google-services.json` into `androidApp/`.
- [ ] `androidApp/build.gradle.kts` → uncomment `alias(libs.plugins.googleServices)` and `alias(libs.plugins.crashlytics)`.
- [ ] `androidApp/.../MainApplication.kt` → uncomment `FirebaseApp.initializeApp`.
- [ ] `app/src/commonMain/kotlin/.../AppInitializer.kt` → add `CrashlyticsLogWriter` (`app/src/androidMain`) to the Kermit log writers.
- [ ] *Optional* — iOS: add the Firebase iOS SDK to `iosApp` with Swift Package Manager, `GoogleService-Info.plist`, call `FirebaseApp.configure()` in `iOSApp.swift`, and provide an iOS `ScreenAnalytics` instead of the logging one in `presentation/analytics/src/nonAndroidMain`.

### 5️⃣ Ads — AdMob <sup>`optional`</sup>

- [ ] Create the AdMob app and a banner ad unit.
- [ ] `androidApp/src/main/AndroidManifest.xml` → uncomment the `com.google.android.gms.ads.APPLICATION_ID` meta-data and set the AdMob app id.
- [ ] `presentation/features/advert/src/androidMain/.../AdvertBanner.android.kt` → set `AD_UNIT_ID_BANNER` and uncomment the `AdView` block (and its imports).
- [ ] `androidApp/build.gradle.kts` and `presentation/features/advert/build.gradle.kts` → uncomment `implementation(libs.google.services.ads)`, and `MobileAds.initialize` in `MainActivity`.

> [!TIP]
> Not using ads? Delete the `:presentation:features:advert` module instead.

### 6️⃣ Local storage <sup>`optional`</sup>

- [ ] Room: uncomment `data/sourcesImpl/local/src/nonWebMain/.../AppRoomDatabase.kt` and the `Room.databaseBuilder` provider of each platform in `di/LocalModule.<platform>.kt`, then add entities and DAOs. Room does not run in the browser: give the web its own in-memory data source.
- [ ] DataStore file name: `DATA_STORE_FILE_NAME` in `di/LocalModule.kt`.

### 7️⃣ Release <sup>`required before publishing`</sup>

- [ ] Android: add a `signingConfig` for `release` (keystore kept out of git, e.g. read from `local.properties` or CI secrets) and review `androidApp/proguard-rules.pro` with a minified `assembleRelease` build.
- [ ] iOS: signing and provisioning in Xcode (`TEAM_ID`).
- [ ] Desktop: `./gradlew :desktopApp:packageDistributionForCurrentOS` (signing and notarization per OS).
- [ ] Web: `./gradlew :webApp:wasmJsBrowserDistribution` and deploy `webApp/build/dist/wasmJs/productionExecutable`.
- [ ] CI runs on `main` (`.github/workflows/ci.yml`); change the branch if yours differs.

---

## 🗂 Project structure

Build configuration lives in the `build-logic` included build as convention plugins (`convention.*`), applied by each module through `plugins { id("convention.…") }`. Modules depend on each other through type-safe project accessors (`implementation(projects.domain.model)`).

```text
.
├── androidApp/              → Android host: Activity, Application, splash, R8
├── iosApp/                  → Xcode project: SwiftUI host of the "Shared" framework
├── desktopApp/              → Desktop host: window and installers
├── webApp/                  → Web host: index.html (Wasm and JS)
├── app/                     → Composition root shared by every platform: DI graph, logger, app shell, navigation host
├── domain/
│   ├── model/               → Business models, Result, Error, AppLogger
│   ├── repository/          → Repository contracts
│   └── useCase/             → Use cases
├── data/
│   ├── repositoryImpl/      → Repository implementations
│   ├── sources/             → Data source contracts
│   └── sourcesImpl/
│       ├── remote/          → Ktor client, DTOs and mappers
│       └── local/           → Room / DataStore (not on the web)
├── presentation/
│   ├── common/              → Theme, resources, shared UI, base ViewModels, navigation keys
│   ├── analytics/           → Analytics abstraction
│   ├── testing/             → Test helpers for commonTest (screenshots, UI test base)
│   └── features/
│       ├── home/            → Sample feature
│       └── advert/          → AdMob banner
└── build-logic/             → Convention plugins
```

| Convention plugin | Applies | Used by |
| --- | --- | --- |
| `convention.detekt` | Detekt + ktlint | every module |
| `convention.kmp.library` | Kotlin Multiplatform without the Android plugin (JVM, iOS, JS, Wasm), coroutines, serialization, Koin, test libraries | `domain:*`, `data:sources`, `data:repositoryImpl`, `data:sourcesImpl:remote` |
| `convention.kmp.android.library` | + Android target | `data:sourcesImpl:local`, `presentation:analytics` |
| `convention.kmp.compose.library` | + Compose Multiplatform, resources, lifecycle, Navigation 3, Koin Compose, UI tests | `presentation:common`, `presentation:testing`, `app` |
| `convention.kmp.feature` | + domain, common, analytics and testing dependencies, Roborazzi | `presentation:features:*` |
| `convention.kmp.environment` | BuildKonfig `dev` / `pre` / `pro` fields | `data:sourcesImpl:remote` |
| `convention.android.application` | Android app, identity from `gradle.properties`, desugaring, R8 | `androidApp` |

---

## 🧰 Tooling

### ▶️ Running the apps

- Android: `./gradlew :androidApp:installDebug`, or the run configuration in Android Studio.
- iOS: open `iosApp/iosApp.xcodeproj` in Xcode and run it (the build phase compiles the `Shared` framework with Gradle).
- Desktop: `./gradlew :desktopApp:run` (hot reload: `./gradlew :desktopApp:hotRun --auto`).
- Web: `./gradlew :webApp:wasmJsBrowserDevelopmentRun` (older browsers: `:webApp:jsBrowserDevelopmentRun`).

### 🔍 Static analysis (Detekt)

```bash
./gradlew detekt
```

Add `-PdetektAutoCorrect=true` to let the ktlint ruleset fix issues in place. Shared rules live in [`config/detekt/detekt.yml`](config/detekt/detekt.yml).

### 📦 Dependency updates

```bash
./gradlew dependencyUpdates --no-parallel
```

> [!NOTE]
> The versions plugin supports neither parallel execution nor the configuration cache, which the task opts out of.

### 🧪 Tests

Tests are written once in `commonTest` and run on every target; platform-specific ones live in the platform test source set.

| Kind | Where | Tools |
| --- | --- | --- |
| Unit | Every module, `src/commonTest` (`Result`, `AppLogger`, `cachedRemoteResource`, use cases, repositories, base ViewModels, `NavBackStack` extensions, `MainViewModel`) | `kotlin.test`, coroutines-test, Turbine, hand-written fakes |
| Integration | `data:sourcesImpl:remote` (the real `remoteModule` Ktor stack against `MockEngine`, JSON decoding) | Ktor MockEngine |
| Koin graph | `app/src/jvmTest/.../DIModulesTest.kt`: verifies every definition of `appComponent` | Koin `verify` |
| Screen | `presentation:features:*`, `src/commonTest` (Compose UI tests with an isolated Koin context) | Compose Multiplatform UI test (Robolectric on Android) |
| Screenshot regression | `presentation/features/home/src/commonTest/screenshots/<platform>/` (reference images in the repo) | Roborazzi (Android, desktop, iOS experimental) |

```bash
# JVM, Android (Robolectric), iOS simulator and Node (JS/Wasm) tests
./gradlew jvmTest testAndroidHostTest iosSimulatorArm64Test jsNodeTest wasmJsNodeTest

# Browser tests run in headless Chrome (installed on CI); Compose UI tests run on Wasm only
./gradlew jsBrowserTest wasmJsBrowserTest

# Screenshot regression: compare against the reference images / re-record them after an intended UI change
./gradlew verifyRoborazziAndroidHostTest verifyRoborazziJvm verifyRoborazziIosSimulatorArm64
./gradlew recordRoborazziAndroidHostTest recordRoborazziJvm recordRoborazziIosSimulatorArm64
```

### ⚙️ Continuous integration

Every push/PR to `main` runs [`.github/workflows/ci.yml`](.github/workflows/ci.yml) on JDK 25:

| Job | Runner | Checks |
| --- | --- | --- |
| Detekt | Ubuntu | `./gradlew detekt` |
| Tests | Ubuntu | JVM, Android, Node and browser (headless Chrome) tests, Android screenshot verification |
| macOS | macOS | iOS simulator tests, iOS and desktop screenshot verification, `iosApp` build |
| Build | Ubuntu | Android debug APK, desktop and web distributions |

Desktop screenshots are recorded and verified on macOS: Skia renders text differently on Linux. Screenshot comparison tolerates a tiny share of changed pixels (anti-aliasing differences between simulator and driver versions), not a changed text or component.

To reproduce the checks locally on macOS (Gradle provisions JDK 25 for the daemon and the toolchain if it is not installed, see `gradle/gradle-daemon-jvm.properties`):

```bash
./gradlew detekt jvmTest testAndroidHostTest jsNodeTest wasmJsNodeTest verifyRoborazziAndroidHostTest verifyRoborazziJvm :androidApp:assembleDebug :desktopApp:compileKotlin :webApp:wasmJsBrowserDistribution --continue
```

---

## 📄 License

This project is licensed under the **MIT License** — you are free to use, copy, modify and distribute it, including for commercial purposes, as long as the copyright notice is kept. See [`LICENSE`](LICENSE) for details.

<div align="center">
<sub>Made with ❤️ by <a href="https://github.com/Samuel-Ambrosio">Samuel Ambrosio</a></sub>
</div>
