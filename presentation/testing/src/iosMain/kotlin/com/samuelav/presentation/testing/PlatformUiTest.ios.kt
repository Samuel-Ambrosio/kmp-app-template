package com.samuelav.presentation.testing

import platform.Foundation.NSUserDefaults

// Compose resources follow the simulator's preferred languages: fixed so the screenshots do not depend on the machine
actual abstract class PlatformUiTest {
    init {
        NSUserDefaults.standardUserDefaults.setObject(listOf("en-US"), forKey = "AppleLanguages")
    }
}
