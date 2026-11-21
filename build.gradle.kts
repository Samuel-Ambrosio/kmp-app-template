import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootEnvSpec

// Puts every plugin on the build classpath once, so all projects share the same classloader.
// The convention plugins in build-logic only compile against them.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.buildKonfig) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.checkVersions)
}

// The Kotlin/JS and Kotlin/Wasm plugins add their own download repositories unless the base URL is
// unset; settings.gradle.kts declares them instead, so FAIL_ON_PROJECT_REPOS can stay on.
@OptIn(ExperimentalWasmDsl::class)
allprojects {
    plugins.withType<NodeJsPlugin> { the<NodeJsEnvSpec>().downloadBaseUrl.set(null as String?) }
    plugins.withType<WasmNodeJsPlugin> { the<WasmNodeJsEnvSpec>().downloadBaseUrl.set(null as String?) }
    plugins.withType<YarnPlugin> { rootProject.the<YarnRootEnvSpec>().downloadBaseUrl.set(null as String?) }
    plugins.withType<WasmYarnPlugin> { rootProject.the<WasmYarnRootEnvSpec>().downloadBaseUrl.set(null as String?) }
    plugins.withType<BinaryenPlugin> { the<BinaryenEnvSpec>().downloadBaseUrl.set(null as String?) }
}

// Run with: ./gradlew dependencyUpdates --no-parallel
tasks.withType<DependencyUpdatesTask>().configureEach {
    notCompatibleWithConfigurationCache("gradle-versions-plugin does not support the configuration cache")
    // Only suggest pre-releases for dependencies that are already on a pre-release (e.g. detekt).
    rejectVersionIf { isNonStable(candidate.version) && !isNonStable(currentVersion) }
}

fun isNonStable(version: String): Boolean {
    val hasStableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val isPlainNumber = "^[0-9,.v-]+(-r)?$".toRegex().matches(version)
    return !(hasStableKeyword || isPlainNumber)
}
