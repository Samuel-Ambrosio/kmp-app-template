import com.samuelav.buildlogic.appIdentity
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("convention.detekt")
}

kotlin {
    jvmToolchain(libs.versions.jvmTarget.get().toInt())
}

dependencies {
    implementation(projects.app)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlin.coroutines.swing)
}

// App identity is configured in gradle.properties
compose.desktop {
    application {
        mainClass = "com.samuelav.desktopapp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = appIdentity.name
            packageVersion = appIdentity.versionName
            macOS {
                bundleID = appIdentity.applicationId
            }
        }
    }
}
