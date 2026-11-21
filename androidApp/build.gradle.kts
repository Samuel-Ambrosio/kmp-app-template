import com.samuelav.buildlogic.Namespaces

plugins {
    id("convention.android.application")
    // TODO: Remove '//' after google-services.json file is added
    //alias(libs.plugins.googleServices)
    //alias(libs.plugins.crashlytics)
}

android {
    namespace = Namespaces.AndroidApp
}

dependencies {
    implementation(projects.app)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splash.screen)
    implementation(libs.koin.android)

    // TODO: Remove '//' after google-services.json file and ad id are added
    //implementation(libs.google.services.ads)
}
