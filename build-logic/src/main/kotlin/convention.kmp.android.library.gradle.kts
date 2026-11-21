import com.samuelav.buildlogic.intVersion
import com.samuelav.buildlogic.libs

// Kotlin Multiplatform library with an Android target, for modules that need Android APIs or
// Android resources in androidMain. The namespace is set by each module (Namespaces.kt).

plugins {
    id("convention.kmp.library")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    android {
        compileSdk = libs.intVersion("android.compileSdk")
        minSdk = libs.intVersion("android.minSdk")

        withHostTest {
            isIncludeAndroidResources = true
        }
    }
}
