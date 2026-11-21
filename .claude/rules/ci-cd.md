---
paths:
  - ".github/workflows/*.yml"
  - "config/detekt/**"
---

When working with GitHub Actions workflows or the static analysis configuration:

## Current workflow — `ci.yml`

Runs on every `push` and PR to `main`, with `concurrency` cancelling previous runs of the same branch. Four parallel jobs, Temurin 25 and `actions/setup-java`'s `cache: gradle`:

| Job | Runner | Command | Artifact |
|---|---|---|---|
| Detekt | `ubuntu-latest` | `./gradlew detekt --continue` | `detekt-reports` (HTML) |
| Tests | `ubuntu-latest` | `jvmTest testAndroidHostTest jsNodeTest wasmJsNodeTest jsBrowserTest wasmJsBrowserTest`, then `verifyRoborazziAndroidHostTest` in its own invocation | `test-reports` (+ Roborazzi diffs) |
| macOS | `macos-latest` | `iosSimulatorArm64Test verifyRoborazziIosSimulatorArm64 verifyRoborazziJvm` + `xcodebuild` of `iosApp` without signing | `ios-test-reports` |
| Build apps | `ubuntu-latest` | `:androidApp:assembleDebug :desktopApp:compileKotlin :webApp:wasmJsBrowserDistribution :webApp:jsBrowserDistribution` | — |

The jobs that build Android only install `platform-tools` with `android-actions/setup-android`: Gradle downloads the platform it needs. Browser tests use the Chrome preinstalled on the Ubuntu runner (Karma + ChromeHeadless). Everything iOS needs a macOS runner: keep the macOS job the only one on macOS (it is the expensive one). Desktop screenshots are verified there too, because they are recorded on macOS and Skia renders glyphs differently on Linux. A Roborazzi verify task switches the test task of that target to compare mode, so screenshot verification runs in its own Gradle invocation, apart from the plain test run.

To reproduce the Ubuntu checks locally, see "Verification" in `.claude/CLAUDE.md`. If you change the CI commands, also update that command and the README's "Continuous integration" section: there cannot be two different sources of truth.

## Rules

- **JDK**: the workflows' `java-version` = `jvmTarget` in `libs.versions.toml` = `gradle/gradle-daemon-jvm.properties`. They change together.
- **Action versions**: pin the major version (`actions/checkout@v7`) and update them all at once.
- **Screenshot references** are verified on CI, never recorded there: a failing `verifyRoborazzi*` means re-recording locally on the affected platform and committing the images, after looking at the diff the job uploads. The comparison tolerates `SCREENSHOT_CHANGE_THRESHOLD` (`presentation:testing`, 0.01 % of pixels) for anti-aliasing noise between simulator/driver versions; do not raise it to make a real difference pass.
- **NEVER hardcode secrets** in a workflow: always `${{ secrets.XXX }}`, passed through `env:` and not as command-line arguments.
- **Release signing** (Android keystore, Apple certificates and profiles, desktop notarization): does not exist yet. When added, the secrets go in GitHub secrets, are decoded on the runner and never enter the repo.
- **Detekt**: shared rules live in `config/detekt/detekt.yml` (on top of the default config, `buildUponDefaultConfig`), applied to every source set of every module (`convention.detekt`). Fix the code before relaxing a rule; if a rule is disabled, do it in the YAML with its reason, not with scattered `@Suppress`. A new custom intermediate source set is added to `MatchingDeclarationName.multiplatformTargets`. `-PdetektAutoCorrect=true` lets the ktlint ruleset fix formatting.
