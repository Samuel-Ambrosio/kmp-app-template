import com.android.build.api.dsl.ApplicationExtension
import com.samuelav.buildlogic.appIdentity
import com.samuelav.buildlogic.intVersion
import com.samuelav.buildlogic.libs
import com.samuelav.buildlogic.library
import com.samuelav.buildlogic.version

// Android host: the launcher Activity and Android-only startup. Kotlin is built into AGP 9.

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("convention.detekt")
}

extensions.configure<ApplicationExtension> {
    compileSdk = libs.intVersion("android.compileSdk")

    // App identity is configured in gradle.properties
    defaultConfig {
        applicationId = appIdentity.applicationId
        minSdk = libs.intVersion("android.minSdk")
        targetSdk = libs.intVersion("android.targetSdk")
        versionCode = appIdentity.versionCode
        versionName = appIdentity.versionName
        resValue("string", "app_name", appIdentity.name.replace("'", "\\'"))
    }

    buildFeatures {
        compose = true
        resValues = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.toVersion(libs.version("jvmTarget"))
        targetCompatibility = JavaVersion.toVersion(libs.version("jvmTarget"))
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    "coreLibraryDesugaring"(libs.library("android.desugar.jdk.libs"))
}
