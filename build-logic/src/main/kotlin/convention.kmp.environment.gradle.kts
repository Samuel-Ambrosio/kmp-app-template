import com.codingfeline.buildkonfig.compiler.FieldSpec.Type

// Backend environments, the multiplatform replacement of Android product flavors: Android flavors
// do not reach shared code, iOS, desktop or web. Pick one with the `buildkonfig.flavor` Gradle
// property (dev by default, see gradle.properties): `./gradlew ... -Pbuildkonfig.flavor=pro`.
// Each module that applies it sets its own `buildkonfig { packageName = ... }`.

plugins {
    id("com.codingfeline.buildkonfig")
}

buildkonfig {
    defaultConfigs {
        buildConfigField(Type.STRING, "ENVIRONMENT", "dev")
        buildConfigField(Type.STRING, "API_BASE_PATH", "https://dummyjson.com/")
        buildConfigField(Type.BOOLEAN, "HTTP_LOGGING", "true")
    }
    defaultConfigs("dev") {
        buildConfigField(Type.STRING, "ENVIRONMENT", "dev")
        buildConfigField(Type.STRING, "API_BASE_PATH", "https://dummyjson.com/")
        buildConfigField(Type.BOOLEAN, "HTTP_LOGGING", "true")
    }
    defaultConfigs("pre") {
        buildConfigField(Type.STRING, "ENVIRONMENT", "pre")
        buildConfigField(Type.STRING, "API_BASE_PATH", "https://dummyjson.com/")
        buildConfigField(Type.BOOLEAN, "HTTP_LOGGING", "true")
    }
    defaultConfigs("pro") {
        buildConfigField(Type.STRING, "ENVIRONMENT", "pro")
        buildConfigField(Type.STRING, "API_BASE_PATH", "https://dummyjson.com/")
        buildConfigField(Type.BOOLEAN, "HTTP_LOGGING", "false")
    }
}
