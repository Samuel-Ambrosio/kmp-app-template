import com.samuelav.buildlogic.Namespaces

plugins {
    id("convention.kmp.feature")
}

kotlin {
    android {
        namespace = Namespaces.Presentation.Features.Home
    }
}
