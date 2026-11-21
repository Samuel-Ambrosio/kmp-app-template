package com.samuelav.presentation.testing

import java.util.Locale

// Compose resources follow the JVM default locale: fixed so the screenshots do not depend on the machine
actual abstract class PlatformUiTest {
    init {
        Locale.setDefault(Locale.US)
    }
}
