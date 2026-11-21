# Engineering principles

These principles apply to the whole codebase. When a design decision violates them, point it out before implementing and propose the right alternative (see `proactive-critique.md`).

---

## SOLID

### S — Single Responsibility
Each class has a single reason to change. A ViewModel does not map DTOs; a mapper (`data:sourcesImpl:remote`) has no business logic; an `actual` only adapts a platform API.

### O — Open/Closed
Extend behavior by adding new code, not by modifying existing code. Prefer exhaustive sealed classes + extension functions over type conditionals scattered around.

### L — Liskov Substitution
A subtype must be able to replace its supertype without breaking the expected behavior. A fake used in tests must be able to take the place of the real implementation in the Koin graph without the rest of the graph noticing, and every `actual` must honor the same contract on every target.

### I — Interface Segregation
Small, cohesive interfaces. Prefer several specific contracts (`data:sources` defines one contract per data source, e.g. `TodoRemoteDataSource`) over one big generic one.

### D — Dependency Inversion (DIP)

- High-level modules (ViewModel, UseCase) do not depend on concrete implementations: they depend on abstractions (`domain:repository`/`data:sources` contracts, `ScreenAnalytics`).
- Dependencies are injected through the constructor with Koin (`get()`), never resolved inside a method.

Koin enforces this structurally: `viewModel { MainViewModel(getAllTodosUseCase = get()) }` gives the class body no way to reach a dependency without going through the DI graph. The most likely violations in this project are **injecting a generic Android `Context` where a dedicated abstraction should be injected**, and **calling a platform API or an `expect fun` directly from a ViewModel or UseCase** instead of receiving an abstraction: watch for both.

---

## KISS, YAGNI, DRY

- **KISS (Keep It Simple)**: the simplest solution that solves the real problem beats the most "elegant" or "flexible" one. Three repeated lines are better than a premature abstraction to avoid them.
- **YAGNI (You Aren't Gonna Need It)**: do not build for a hypothetical future case. A constant, module, `expect` declaration or parameter with no real consumer is deleted, not completed "just in case".
- **DRY (Don't Repeat Yourself)**: a business rule has a single source. In KMP this also means it lives in `commonMain`, never reimplemented per platform.

**KISS/YAGNI are not a license to ignore SOLID when the real case already calls for it**: they are the criterion that decides *when* to apply an abstraction (there is a second real consumer that justifies it) and when not to.

---

## Clean Architecture — Dependency Rule and module placement

Dependencies always point inwards. Graph as defined by the project's actual `build.gradle.kts` files:

```
domain:model  (no internal deps, only kotlinx.serialization)
     ▲
     ├── domain:repository  (domain:model)
     ├── domain:useCase     (domain:model + domain:repository)
     └── data:sources       (domain:model, contracts)
              ▲
              ├── data:sourcesImpl:remote / :local  (domain:model + data:sources)
              └── data:repositoryImpl
                      (domain:model + domain:repository + data:sources)

presentation:common, presentation:analytics
presentation:features:*  (domain:model + domain:useCase + presentation:common + presentation:analytics,
                          through the `convention.kmp.feature` convention plugin)

app  (composition root shared by every platform: depends on everything above)
androidApp, desktopApp, webApp, iosApp  (platform hosts: only depend on app)
```

Neither `data:sources` nor `data:repositoryImpl` ever imports a third-party SDK directly or a `presentation:*` type. The `domain:*`, `data:sources`, `data:repositoryImpl` and `data:sourcesImpl:remote` modules are pure Kotlin Multiplatform (`convention.kmp.library`): no Android target, no platform API in `commonMain`.

The real risk is not a direct outward import, but that nothing stops someone from adding a new dependency without thinking. **Any new entry in the `dependencies {}` of a `build.gradle.kts`, especially in `domain:*`/`data:sources`, is architecturally significant**: check it against this graph and check that the library supports every declared target, not just that it compiles.

---

## Screaming Architecture

The module/folder structure must communicate the business domain, not the framework. Inside each module, one folder per business concept (`.../todo/`), not "Kotlin code lives here". If a feature module (`presentation:features:home`) contains business logic that is not UI, the folder no longer screams what it is: that logic belongs in `domain:useCase`.

---

## Codebase precedent before theory

Before treating a placement or design decision as ambiguous, look for an established pattern in the project: theory (Clean Architecture, SOLID) breaks ties when there is no precedent, not before. Grep for similar naming/module conventions before concluding "there is no precedent for this": the right precedent may live one level up or in a sibling module.

Note: this is a template, and part of the code is sample code (the *Todo* feature against DummyJSON). A template sample is a starting point, not a standard (see `proactive-critique.md`).

---

## Testability

- All code that touches Firebase, the network, disk, time or a platform API must be able to receive a substitute through injection (Koin `get()`, overridden in tests with a fake, e.g. `MockEngine` for `HttpClientEngine`).
- If logic can only be tested on one platform, it is in the wrong source set or it violates DIP.
- Tests live in `commonTest` of the module of the code under test, so they run on every target, and only use libraries declared in `libs.versions.toml` (add a missing one there). The README's "Tests" section lists each kind and where its reference lives: follow the existing one before writing a new kind.
- Prefer hand-written fakes of the contract (`FakeTodoRepository`) over mocks: reflection-based mocking libraries do not work on Native or Wasm.
- A new Koin definition must keep `DIModulesTest` (`app/src/jvmTest`) green.
- A UI change that alters a Roborazzi reference image is re-recorded on the three platforms in the same change, after checking the new images are the intended ones.

---

## Single Source of Truth (SSOT)

A business rule has **a single canonical implementation**. The `Result<F, S>` wrapper (`domain/model/.../utils/Result.kt`) is the established instance of this principle at the Data → Domain → Presentation boundary (see `feature-modules.md`). If two features need the same business criterion, both call the same UseCase: the criterion is never reimplemented per feature or per platform.

---

## Composition over inheritance

Prefer interfaces + composition over subclasses when the shared behavior does not need shared state/lifecycle. Inheritance is justified only when there is a real `is-a` relationship and the base class controls the lifecycle. In this project that is the only justification: `BaseViewModel`/`ColdStateBaseViewModel` (see `viewmodel.md`). What they share without needing a lifecycle goes through composition: the `CommandSource` interface, which is all `CommandHandler` knows about. Do not introduce another inheritance hierarchy without the same justification. `PlatformUiTest` (test-only) exists because JUnit's `@RunWith` can only reach common tests through a base class.

---

## Vocabulary of patterns already in use (GoF)

Naming them does not change the code: it gives a shared vocabulary to recognize when a new case is a variant of something that already exists, instead of inventing an ad-hoc solution.

| Already in the project | Pattern (GoF) | Why |
|---|---|---|
| UseCase (`domain:useCase`, `operator fun invoke`) | Command | Encapsulates a request as an object with a single entry point |
| `BaseViewModel`/`ColdStateBaseViewModel` (base classes, control the lifecycle; `onInitState()` is the step each subclass fills in) | Template Method | The only valid justification for inheritance in this project |
| `Result<F, S>` (Loading/Success/Failure) | State | The screen's behavior changes with the state of the load |
| `Network*.to{Domain}()` mapper (`data:sourcesImpl:remote`) | Adapter | Translates the external contract (API) into the internal Domain model (see `data-layer.md`) |
| `HttpClient.requestResult` (Ktor) | Adapter | Turns the HTTP response into a `Result<Error, T>` |
| `actual` implementation of a common contract (`defaultHttpClientEngine`, `platformLocalModule`) | Strategy | The platform implementation is chosen per target, not inside the caller |
| Abstraction injected through Koin `get()` in the constructor | Strategy | The concrete implementation is chosen at the injection point, not inside the type |
| `StateFlow`/`SharedFlow` (`state`/`command` of the ViewModel base classes) | Observer | Notifies state changes/events to subscribers |
| Compose `Modifier` (`.padding().background()...`) | Decorator | Each `Modifier` wraps the previous one without modifying the base Composable |

**Before settling an architecture decision "by judgment", first check whether it fits one of these** (or the rest of the GoF catalog, only when there is real evidence of need, never upfront).

**What NOT to add without evidence**: Visitor, Flyweight, Bridge, Composite, Prototype, Mediator, Iterator, Memento, Builder, Abstract Factory. Documenting or introducing them without a real consumer would be the very Speculative Generality that `proactive-critique.md` asks to avoid.

**Singleton**: last resort, not a neutral pattern (see DIP above). `AppLogger` (`object`) is the legitimate exception: it is a cross-cutting infrastructure facade (logging), not a business dependency that should be injectable/mockable. `AppDispatchers` (`expect object`) is the other one: it only picks the platform dispatcher, and every consumer takes the dispatcher as a constructor parameter defaulting to it. Do not use `object` for anything that needs to be replaced in tests.

---

## Code comments — only when asked

Zero new inline comments or KDoc unless the user explicitly asks for them. The code explains itself; if a decision needs justification, it goes in the PR description or the conversation, not in the file. The exception is a platform constraint that the code cannot express and that someone would otherwise "fix" (why web has no `Dispatchers.IO`, why iOS screenshots need an absolute path): one line, next to the code it explains.

---

## A green build does not guarantee correct runtime

A green build compiles successfully even if the app may crash at runtime (an uncaught exception during startup, a dependency missing from the Koin graph that is only resolved when the screen opens, a missing R8 rule in release), and in KMP it only proves the targets that were compiled: `:androidApp:assembleDebug` says nothing about `iosMain`, `jvmTest` says nothing about `wasmJs`.

When runtime really matters (after touching `startApp`, the hosts, the Koin graph, `expect`/`actual`, R8 or `AndroidManifest.xml`), validate with more than the build: run the tests of each affected target and launch the real app (Android emulator, iOS simulator, desktop window, browser).
