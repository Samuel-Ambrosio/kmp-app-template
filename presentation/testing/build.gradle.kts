import com.samuelav.buildlogic.Namespaces
import org.jetbrains.compose.ComposePlugin

// Test-only helpers shared by the feature modules: only ever added to commonTest.
// Screenshots go through Roborazzi on Android (Robolectric), desktop and iOS (experimental);
// the web has no Roborazzi support, so capturing is a no-op there.

plugins {
    id("convention.kmp.compose.library")
}

kotlin {
    android {
        namespace = Namespaces.Presentation.Testing
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.ui.test)
        }
        androidMain.dependencies {
            api(libs.test.junit)
            api(libs.test.robolectric)
            api(libs.test.roborazzi)
            api(libs.test.roborazzi.compose)
            api(libs.test.android.compose.ui.test.junit4)
            api(libs.test.android.compose.ui.test.manifest)
            // The Espresso that ui-test-junit4 brings transitively predates the SDK Robolectric runs on
            api(libs.test.android.espresso)
        }
        jvmMain.dependencies {
            api(libs.test.roborazzi.compose.desktop)
            api(ComposePlugin.DesktopDependencies.currentOs)
            api(libs.kotlin.coroutines.swing)
        }
        iosMain.dependencies {
            api(libs.test.roborazzi.compose.ios)
        }
    }
}
