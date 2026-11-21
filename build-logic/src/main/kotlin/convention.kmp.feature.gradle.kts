// Feature module: a Compose Multiplatform library that already depends on the domain contracts, the
// shared UI and analytics. Screen and screenshot tests live in commonTest and run on every target.

plugins {
    id("convention.kmp.compose.library")
    id("io.github.takahirom.roborazzi")
}

kotlin {
    // Under Karma, Skiko is not initialized for the JS target ("org_jetbrains_skia_* is not defined"), so
    // Compose UI tests cannot render there. The web UI tests run on Wasm, the primary web target.
    js {
        browser { testTask { enabled = false } }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:model"))
            implementation(project(":domain:useCase"))
            implementation(project(":presentation:analytics"))
            implementation(project(":presentation:common"))
        }
        commonTest.dependencies {
            implementation(project(":domain:repository"))
            implementation(project(":presentation:testing"))
        }
    }
}

// Robolectric renders Compose with the real Android graphics stack, needed by Roborazzi on Android.
tasks.withType<Test>().configureEach {
    systemProperty("robolectric.graphicsMode", "NATIVE")
    systemProperty("robolectric.pixelCopyRenderMode", "hardware")
}
