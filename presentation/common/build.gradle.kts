import com.samuelav.buildlogic.Namespaces

plugins {
    id("convention.kmp.compose.library")
}

kotlin {
    android {
        namespace = Namespaces.Presentation.Common
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
        }
    }
}

// Features, the app and the hosts read the shared strings, fonts and icons
compose.resources {
    publicResClass = true
    packageOfResClass = "${Namespaces.Presentation.Common}.resources"
}
