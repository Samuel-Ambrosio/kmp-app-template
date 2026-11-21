package com.samuelav.presentation.common.app.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface AppNavKey : NavKey {
    @Serializable
    sealed interface Home : AppNavKey {
        @Serializable
        data object Main : Home
    }

    @Serializable
    sealed interface Search : AppNavKey {
        @Serializable
        data object Main : Search
    }

    @Serializable
    sealed interface More : AppNavKey {
        @Serializable
        data object Main : More
    }
}

// Outside the JVM there is no reflection: the back stack can only be saved and restored for the
// NavKey subtypes registered here. A sealed AppNavKey registers every destination at once.
@OptIn(ExperimentalSerializationApi::class)
val appNavSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<AppNavKey>()
        }
    }
}
