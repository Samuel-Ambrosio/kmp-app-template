import com.samuelav.buildlogic.Namespaces
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id("convention.kmp.android.library")
}

kotlin {
    android {
        namespace = Namespaces.Presentation.Analytics
    }

    // Firebase Analytics only has an Android implementation wired in; the rest of the targets log the events.
    applyDefaultHierarchyTemplate {
        common {
            group("nonAndroid") {
                withJvm()
                withIos()
                withJs()
                withWasmJs()
            }
        }
    }

    sourceSets {
        getByName("nonAndroidMain").dependencies {
            implementation(projects.domain.model)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.google.firebase.bom))
            implementation(libs.google.firebase.analytics)
            implementation(libs.androidx.core.ktx)
        }
    }
}
