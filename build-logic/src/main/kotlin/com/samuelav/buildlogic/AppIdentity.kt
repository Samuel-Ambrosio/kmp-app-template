package com.samuelav.buildlogic

import org.gradle.api.Project

/** App identity read from the root `gradle.properties` (`app.*`). The only place to change it. */
data class AppIdentity(
    val name: String,
    val applicationId: String,
    val versionName: String,
    val versionCode: Int,
) {
    /** The name without whitespace, for identifiers that do not allow it (Gradle project, iOS PRODUCT_NAME). */
    val compactName: String = name.replace(Regex("\\s+"), "")
}

val Project.appIdentity: AppIdentity
    get() = AppIdentity(
        name = providers.gradleProperty("app.name").get(),
        applicationId = providers.gradleProperty("app.applicationId").get(),
        versionName = providers.gradleProperty("app.versionName").get(),
        versionCode = providers.gradleProperty("app.versionCode").get().toInt(),
    )
