import com.samuelav.buildlogic.libs
import com.samuelav.buildlogic.library
import org.jetbrains.compose.ComposePlugin

// Compose Multiplatform library. Web tests render with Skiko, so they need a browser: they run in
// Karma with headless Chrome (CI has it). Skiko is only bundled into the test executable when the
// target declares an executable binary (CMP-4906).

plugins {
    id("convention.kmp.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    android {
        androidResources {
            enable = true
        }
    }
    js {
        browser { testTask { enabled = true; useKarma { useChromeHeadless() } } }
        nodejs { testTask { enabled = false } }
        binaries.executable()
    }
    wasmJs {
        browser { testTask { enabled = true; useKarma { useChromeHeadless() } } }
        nodejs { testTask { enabled = false } }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.library("compose.runtime"))
            implementation(libs.library("compose.foundation"))
            implementation(libs.library("compose.ui"))
            implementation(libs.library("compose.material3"))
            implementation(libs.library("compose.components.resources"))
            implementation(libs.library("compose.ui.tooling.preview"))

            implementation(libs.library("lifecycle.viewmodel"))
            implementation(libs.library("lifecycle.viewmodel.compose"))
            implementation(libs.library("lifecycle.runtime.compose"))
            implementation(libs.library("lifecycle.viewmodel.navigation3"))
            implementation(libs.library("navigation3.ui"))

            implementation(libs.library("koin.compose"))
            implementation(libs.library("koin.compose.viewmodel"))
        }
        androidMain.dependencies {
            implementation(libs.library("compose.ui.tooling"))
        }
        commonTest.dependencies {
            implementation(libs.library("compose.ui.test"))
        }
        jvmTest.dependencies {
            // Desktop UI tests need the Skiko natives of the host OS and a Main dispatcher.
            implementation(ComposePlugin.DesktopDependencies.currentOs)
            implementation(libs.library("kotlin.coroutines.swing"))
        }
    }
}
