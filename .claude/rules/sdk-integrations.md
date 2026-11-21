---
paths:
  - "presentation/analytics/**/*.kt"
  - "presentation/features/advert/**/*.kt"
  - "data/sourcesImpl/**/*.kt"
  - "app/src/**/*.kt"
  - "androidApp/**/*.kt"
  - "iosApp/**/*.swift"
---

When working with third-party SDK integrations (Firebase, AdMob, Ktor, Room, DataStore, Kermit):

## The boundary: never in `domain:*` or `data:repositoryImpl`

No third-party SDK import in `domain/**`, `data/sources/**` or `data/repositoryImpl/**`: they are pure Kotlin and only know contracts and domain models. Each SDK lives in the module that matches its layer:

| SDK | Where it lives | Why |
|---|---|---|
| Ktor, kotlinx.serialization | `data:sourcesImpl:remote` (engine per platform) | Implements the `*RemoteDataSource` contracts from `data:sources` |
| Room, DataStore | `data:sourcesImpl:local`, `nonWebMain` | Implements the `*LocalDataSource` contracts; no browser support |
| Firebase Analytics | `presentation:analytics`, `androidMain` | Behind the `ScreenAnalytics` contract |
| Kermit, Firebase Crashlytics | `app` (composition root; Crashlytics in `androidMain`) | Cross-cutting startup infrastructure (`startApp`), behind `Logger`/`AppLogger` |
| Google Mobile Ads | `presentation:features:advert`, `androidMain` (+ `androidApp` for initialization) | Isolated UI component behind `expect fun AdvertBanner`; the whole module can be deleted |

Do not create a new module per SDK as a rule: if the product has its own data contract it goes in the matching `data:sourcesImpl:*`; if it is startup infrastructure it goes in `app`; if it is isolated UI, in its feature.

## Platform support decides the source set

Before adding an SDK, check which targets it really supports (a published artifact is not a working implementation: some multiplatform wrappers only ship stubs on JVM/web). An SDK with no implementation on a target lives in the source set of the targets that have one (`androidMain`, `nonWebMain`…), and the rest get a fallback implementation of the same contract (log, no-op, in-memory), as `LogScreenAnalytics` and the empty web `platformLocalModule` do. An iOS SDK that has to be linked from Xcode (Firebase, AdMob) is not added to `iosMain` by default: it would break the iOS build of everyone who has not set it up. Document the steps in the README instead.

## Naming

1. **`{Contract}Impl`**: implementation of one of our contracts (`TodoRemoteDataSourceImpl`, `ScreenAnalyticsImpl`).
2. **`{Sdk}{Thing}`**: adapter from an SDK to one of our abstractions, without the `Impl` suffix (`KermitLogger : Logger`, `CrashlyticsLogWriter`, `CoreFirebaseAnalytics`).

## DI and activation

Each integration exposes its `val {x}Module = module { ... }` in its Gradle module and is activated in `appComponent`. The pieces that need external configuration (Firebase with `google-services.json`, AdMob with its app id) come commented out with a `TODO`; the steps to enable them are in the README. When adding a new SDK that needs keys, follow the same pattern: disabled by default and documented in the README.

**NEVER** hardcode keys or secrets in Kotlin, Swift or `.xcconfig`: they go in `local.properties`/CI secrets and arrive through BuildKonfig.
