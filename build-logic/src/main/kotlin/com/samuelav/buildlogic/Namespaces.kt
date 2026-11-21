package com.samuelav.buildlogic

object Namespaces {
    const val AndroidApp = "com.samuelav.androidapp"
    const val App = "com.samuelav.app"

    object Data {
        object SourcesImpl {
            const val Local = "com.samuelav.data.sourcesImpl.local"
            const val Remote = "com.samuelav.data.sourcesImpl.remote"
        }
    }

    object Presentation {
        const val Analytics = "com.samuelav.presentation.analytics"
        const val Common = "com.samuelav.presentation.common"
        const val Testing = "com.samuelav.presentation.testing"

        object Features {
            const val Advert = "com.samuelav.presentation.features.advert"
            const val Home = "com.samuelav.presentation.features.home"
        }
    }
}
