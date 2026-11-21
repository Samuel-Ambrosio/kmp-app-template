import com.samuelav.buildlogic.intVersion
import com.samuelav.buildlogic.libs
import com.samuelav.buildlogic.library
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

// Pure Kotlin Multiplatform library: every target the app ships on, no Android plugin.
// Android consumes the JVM variant, so domain and data contracts never pay the Android build cost.

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("convention.detekt")
}

kotlin {
    // Compiles with a JDK of this version (downloaded by foojay if missing) and targets the same bytecode.
    jvmToolchain(libs.intVersion("jvmTarget"))

    // expect object/class are used on purpose (AppDispatchers, PlatformUiTest): they are still Beta
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvm()
    iosArm64()
    iosSimulatorArm64()
    js {
        browser { testTask { enabled = false } }
        // Non-UI tests run on Node: no browser needed locally.
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser { testTask { enabled = false } }
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.library("kotlin.coroutines.core"))
            implementation(libs.library("kotlin.serialization.core"))
            implementation(libs.library("koin.core"))
        }
        commonTest.dependencies {
            implementation(libs.library("test.kotlin"))
            implementation(libs.library("test.coroutines"))
            implementation(libs.library("test.turbine"))
        }
    }
}
