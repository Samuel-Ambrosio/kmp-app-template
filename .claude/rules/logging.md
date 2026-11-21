---
paths:
  - "**/*.kt"
---

# Logging

The project uses `AppLogger` (`domain/model/src/commonMain/.../utils/debug/AppLogger.kt`) as the single logging entry point, on every platform. **Never use `println(...)`, `android.util.Log.*`, `NSLog(...)` or `console.log(...)` directly in application code.**

## API

```kotlin
import com.samuelav.domain.model.utils.debug.AppLogger

AppLogger.v("verbose message")
AppLogger.d("debug message")
AppLogger.i("info message")
AppLogger.w("warning message")
AppLogger.e("error message")

// With an associated throwable
AppLogger.e(throwable, "additional context")
```

`AppLogger` buffers messages until a backend is assigned (`AppLogger.logger = KermitLogger()` in `startApp()`, `app/src/commonMain/.../AppInitializer.kt`), so it is safe to call from any module, including `domain:*` (pure Kotlin), before the app finishes starting.

The backend is `KermitLogger` (`app/src/commonMain/.../utils/debug/KermitLogger.kt`), which forwards to Kermit: Logcat on Android, OSLog on iOS, the console on desktop and web. On Android, `CrashlyticsLogWriter` (`app/src/androidMain`) sends logs to Crashlytics once Firebase is configured (see the README).

## Mandatory rules

- **FORBIDDEN**: `println(...)`.
- **FORBIDDEN**: `android.util.Log.*`, `NSLog(...)`, `console.log/info/warn/error/debug(...)`. If you need new logging, use `AppLogger`; if a platform needs a new destination, add a Kermit `LogWriter` in the `app` source set of that platform.
- **FORBIDDEN**: leaving temporary investigation `.d`/`.v` logs in a final PR: remove them before the final commit. To find them easily, give them a recognizable prefix (`AppLogger.d("[DEBUG] ...")`).
- Tests assert on logging through a fake `Logger` (`AppLoggerTest`), not by capturing stdout.

The `.claude/hooks/debug-log-guard.sh` hook blocks these calls when writing Kotlin files.

## Why

The platform primitives bypass the configured backend (Kermit → Logcat/OSLog/console/Crashlytics) and the startup buffer, and behave differently on each target: a log emitted before `startApp()` configures the backend is lost, whereas with `AppLogger` it is buffered and forwarded as soon as the backend is ready.
