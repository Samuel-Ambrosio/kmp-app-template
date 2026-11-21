package com.samuelav.presentation.testing

/**
 * Base class of every Compose UI test in commonTest. On Android it runs the test with Robolectric,
 * so the same test also runs as a JVM host test; on the rest of the targets it adds nothing.
 */
expect abstract class PlatformUiTest()

// Compose Multiplatform loads strings and fonts asynchronously: the first test of a run can take
// longer than the default 1 s of waitUntil to show them.
const val UI_TEST_TIMEOUT_MILLIS = 5_000L
