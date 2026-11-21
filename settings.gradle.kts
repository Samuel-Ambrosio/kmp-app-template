pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // Lets Gradle download the JDK requested by `jvmToolchain(...)` when it is not installed locally.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()

        // Toolchains the Kotlin/JS and Kotlin/Wasm plugins download. They are declared here because
        // FAIL_ON_PROJECT_REPOS forbids the plugin from adding them (see the root build.gradle.kts).
        exclusiveContent {
            forRepository {
                ivy("https://nodejs.org/dist") {
                    name = "Node.js distributions"
                    patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
                    metadataSources { artifact() }
                }
            }
            filter { includeModule("org.nodejs", "node") }
        }
        exclusiveContent {
            forRepository {
                ivy("https://github.com/yarnpkg/yarn/releases/download") {
                    name = "Yarn distributions"
                    patternLayout { artifact("v[revision]/[artifact](-v[revision]).[ext]") }
                    metadataSources { artifact() }
                }
            }
            filter { includeModule("com.yarnpkg", "yarn") }
        }
        exclusiveContent {
            forRepository {
                ivy("https://github.com/WebAssembly/binaryen/releases/download") {
                    name = "Binaryen distributions"
                    patternLayout { artifact("version_[revision]/binaryen-version_[revision]-[classifier].[ext]") }
                    metadataSources { artifact() }
                }
            }
            filter { includeModule("com.github.webassembly", "binaryen") }
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Configured in gradle.properties (app.name)
rootProject.name = providers.gradleProperty("app.name").get().replace(Regex("\\s+"), "")
include(
    listOf(
        ":androidApp",
        ":app",
        ":data:repositoryImpl",
        ":data:sources",
        ":data:sourcesImpl:local",
        ":data:sourcesImpl:remote",
        ":desktopApp",
        ":domain:model",
        ":domain:repository",
        ":domain:useCase",
        ":presentation:analytics",
        ":presentation:common",
        ":presentation:features:advert",
        ":presentation:features:home",
        ":presentation:testing",
        ":webApp",
    )
)
