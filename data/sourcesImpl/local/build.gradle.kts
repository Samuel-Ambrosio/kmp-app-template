import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import com.samuelav.buildlogic.Namespaces
import com.samuelav.buildlogic.appIdentity
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id("convention.kmp.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildKonfig)
}

buildkonfig {
    packageName = Namespaces.Data.SourcesImpl.Local
    defaultConfigs {
        // Names the desktop storage directory (~/.<applicationId>)
        buildConfigField(Type.STRING, "APPLICATION_ID", appIdentity.applicationId)
    }
}

kotlin {
    explicitApi()

    android {
        namespace = Namespaces.Data.SourcesImpl.Local
    }

    // Room and DataStore files exist on Android, iOS and desktop, not in the browser:
    // nonWebMain holds what they share.
    applyDefaultHierarchyTemplate {
        common {
            group("nonWeb") {
                withCompilations { it.target.platformType == KotlinPlatformType.androidJvm }
                withJvm()
                group("ios") { withIos() }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
            implementation(projects.data.sources)
        }
        getByName("nonWebMain").dependencies {
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.datastore.preferences.core)
            implementation(libs.kotlin.datetime)
            implementation(libs.kotlin.serialization.json)
        }
    }
}

dependencies {
    listOf("kspAndroid", "kspJvm", "kspIosArm64", "kspIosSimulatorArm64").forEach { add(it, libs.room.compiler) }
}
