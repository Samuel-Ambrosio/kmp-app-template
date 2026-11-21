import com.samuelav.buildlogic.Namespaces

plugins {
    id("convention.kmp.library")
    id("convention.kmp.environment")
}

buildkonfig {
    packageName = Namespaces.Data.SourcesImpl.Remote
}

kotlin {
    explicitApi()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
            implementation(projects.data.sources)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlin.serialization.json)
        }
        // Android consumes the JVM variant of this module, so OkHttp is the engine on both.
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        webMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
        }
    }
}
