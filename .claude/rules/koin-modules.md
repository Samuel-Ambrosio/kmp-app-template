---
paths:
  - "**/di/*Module.kt"
  - "**/di/AppComponent.kt"
---

When adding or touching a registration in a Koin module (`single`, `factory`, `viewModel`):

## One Koin module per Gradle module

Each Gradle module exposes a single `val {x}Module = module { ... }` in its `di/` package (`useCaseModule`, `repositoryModule`, `remoteModule`, `localModule`, `analyticsModule`, `feature{X}Module`), and `appComponent` (`app/src/commonMain/.../di/AppComponent.kt`) gathers them. A new module is not active until it is added to `appComponent`. `startApp()` (`app/.../AppInitializer.kt`) starts Koin once; the Android host adds `androidContext`, which is how Android-only definitions (`DataStore` path, `FirebaseAnalytics`) get a `Context`.

## Platform-specific definitions — `expect val platform{X}Module`

When a module needs different definitions per platform, its common module includes an `internal expect val platform{X}Module: Module`, and each platform source set provides the `actual` with its definitions (`platformLocalModule`, `platformAnalyticsModule`). The common contract is the same on every platform, only the implementation changes. Never branch on the platform inside a definition.

## Alphabetical order by registered type

Registrations in a Koin module are sorted alphabetically by the **registered type**, not by when they were added or by affinity with the neighboring registration. A new registration goes in its alphabetical slot, never at the end of the module.

- **Sort key**: the type name without its suffix (`UseCase`, `Repository`, `DataSource`), compared case-sensitively. So `GetTodo` goes before `GetTodos`.
- **`single<Interface> { Impl(...) }`**: sorted by the interface, not the implementation.
- **Exception**: the infrastructure everything else needs (in `remoteModule`: `HttpClientEngine`, `Json`, `HttpClient`) goes first, as a block, followed by services and data sources in alphabetical order.

## Named arguments

Constructors are invoked with named arguments (`TodoRepositoryImpl(todoRemoteDataSource = get())`), as in the rest of the project: a positional `get()` breaks silently if the constructor is reordered and two parameters share a type.

## Verification and tests

`DIModulesTest` (`app/src/jvmTest`) verifies every definition of `appComponent` with Koin `verify()`. A new definition whose constructor takes a type that is not in the graph on purpose (built with its own default) is added to its `extraTypes`, never excluded from the check. Tests that need Koin create their own `koinApplication { }` (or `KoinIsolatedContext` in Compose tests) instead of the global `startKoin`.
