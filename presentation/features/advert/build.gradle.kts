import com.samuelav.buildlogic.Namespaces

plugins {
    id("convention.kmp.feature")
}

kotlin {
    android {
        namespace = Namespaces.Presentation.Features.Advert
    }

    // AdMob only ships an Android SDK wired in here (iOS needs the native SDK linked from Xcode):
    // the banner is empty on the rest of the targets.
    applyDefaultHierarchyTemplate {
        common {
            group("nonAndroid") {
                withJvm()
                withIos()
                withJs()
                withWasmJs()
            }
        }
    }

    sourceSets {
        androidMain.dependencies {
            // TODO: Remove '//' after google-services.json file and ad id are added
            //implementation(libs.google.services.ads)
        }
    }
}
