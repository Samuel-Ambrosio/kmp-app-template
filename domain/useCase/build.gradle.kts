plugins {
    id("convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
            implementation(projects.domain.repository)
        }
    }
}
