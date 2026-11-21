---
paths:
  - "data/**/*.kt"
  - "**/*Repository*.kt"
  - "**/*DataSource*.kt"
---

When working with Repositories, data sources or the `data:sourcesImpl:*` models/mappers (all code in `commonMain` unless it is platform storage):

- Network DTOs (`Network*`, `@Serializable`) and their mapping to Domain live exclusively in `data:sourcesImpl:remote`, never in `data:repositoryImpl`. `data:repositoryImpl` only orchestrates (local/remote/cache) and works with already-mapped Domain objects; it never touches a DTO.
- Services are `internal` classes over the Ktor `HttpClient` (`TodoService`) and return `Result<Error, T>` directly (`suspend fun getTodos(): Result<Error, List<NetworkTodo>>`) by going through `HttpClient.requestResult { … }` (`utils/ResultHttpClient.kt`), the single point that translates status codes, API error bodies, timeouts and parsing/network exceptions into `Error`. The client is built with `expectSuccess = false`. Do not wrap a call in `try/catch` or return `HttpResponse`: if an error case is missing, add it in `requestResult`.
- The client (`remoteModule`) is built once: base URL from `BuildKonfig.API_BASE_PATH`, `ContentNegotiation` with the injected `Json` (`ignoreUnknownKeys`, `explicitNulls = false`), `HttpTimeout`, and `Logging` through `AppLogger` when `BuildKonfig.HTTP_LOGGING`. The engine is a Koin definition (`HttpClientEngine`, `defaultHttpClientEngine()`: OkHttp on JVM/Android, Darwin on iOS, Js on the web), so tests replace it with `MockEngine`.
- `Network*` classes, services and `Impl`s are `internal`: the modules use `explicitApi()` and only expose their Koin module (`remoteModule`/`localModule`).
- Mapper naming: extension function `internal fun Network<X>.to<DomainName>(): DomainType`, next to the DTO for simple cases (`NetworkTodo.toTodo()` in `NetworkTodo.kt`) or in a dedicated `mapper/*.kt` file when the mapping is complex and multi-field.
- **Method order within the same resource group**: alphabetical within the cluster of the same resource, and the same order in the `interface`, its `Impl` and the Ktor service.

## DTO field names — in English, with `@SerialName`

`Network*` properties are written in correctly spelled English and camelCase, whatever the backend returns: misspelled words (`tittle`), underscores instead of camelCase (`end_date`) or names in a language other than English (`preis`). The real JSON name goes in `@SerialName("…")`:

```kotlin
// ✅
@Serializable
internal data class NetworkOffer(
    val id: Long? = null,
    @SerialName("end_date") val endDate: String? = null,  // underscore, not camelCase
    @SerialName("preis") val price: Double? = null,       // not in English
    @SerialName("tittle") val title: String? = null,      // misspelled
)

// ❌ The DTO carries over the backend's naming
internal data class NetworkOffer(val end_date: String?, val preis: Double?, val tittle: String?)
```

- If the field already comes in English and camelCase, no annotation is needed.
- A nullable field needs `= null`: without a default, kotlinx.serialization treats a missing key as an error, not as `null`.
- Dates: `kotlinx.datetime.LocalDate`/`kotlin.time.Instant` with their built-in serializers, never `String` parsed by hand or `java.time`.
- **When you annotate a field, cover the decoding with a test in `commonTest`** (`json.decodeFromString<Network<X>>(json)`, with the `Json` of `remoteModule`, as `NetworkModelsDecodingTest` does): a misspelled `@SerialName` does not fail at compile time; it silently leaves the field `null`.

## Caching in Repositories — `cachedRemoteResource` / `cachedRemoteResourceFlow`

When a Repository serves data from a cache (memory, Room or DataStore) and only goes to the remote when it does not have it, **use the helpers in `domain/repository/.../utils/CachedRemoteResult.kt`**. Do not hand-write `cache?.asSuccess() ?: remote().ifSuccess { save(it) }`.

```kotlin
// ✅ suspend, one-off read
override suspend fun getAll(): Result<Error, List<Todo>> =
    cachedRemoteResource(
        getFromLocal = todoLocalDataSource::getAll,
        getFromRemote = todoRemoteDataSource::getAll,
        saveToLocal = { todos -> todoLocalDataSource.save(todos = todos) },
    )
```

- **`cachedRemoteResource`** (`suspend`, returns `Result<Error, T>`):
  - Reads `getFromLocal()`. If it is `null`, or if `shouldFetch(cached)` is `true`, it calls `getFromRemote()`, saves with `saveToLocal` and returns what it reads again from `getFromLocal()`.
  - A remote failure is not saved, and an exception is logged with `AppLogger` and turned into `Error.Unknown`.
  - **`shouldFetch`** decides when to refresh even if there is a cached value (e.g. `{ it.isEmpty() }`).
  - There is a `<T, R>` overload for when the remote returns a different type from the one read from the cache.
- **`cachedRemoteResourceFlow`** returns a `Flow<Result<Error, T>>` and observes `getFromLocal(): Flow<T?>`. Use it when the cache is observable (Room, DataStore).
- **Careful when testing the Repository**: since the helper reads `getFromLocal()` again after saving, the cache fake/mock must return `null` on the first read and the data on the second.
- **A detail item goes through the helper too.** If saving it where the list is stored would break the list, the local source stores it separately (e.g. a map by id) and the detail read looks in the list first and then there. That way the stored list is not replaced by a single item.

## Data sources — `{X}RemoteDataSource` / `{X}LocalDataSource`

The contract lives in `data:sources` (`{X}RemoteDataSource`, `{X}LocalDataSource`) and its implementation in `data:sourcesImpl:remote`/`:local` (`{X}RemoteDataSourceImpl`, `{X}LocalDataSourceImpl`). The name says where the data comes from, not how it is stored: a local source is called `LocalDataSource` whether it stores in memory, DataStore or Room. Never `{X}CacheDataSource`.

### Local storage per platform

Room and DataStore exist on Android, iOS and desktop, not in the browser: their code lives in `data:sourcesImpl:local/src/nonWebMain`, and each platform provides its database/file path in `di/LocalModule.<platform>.kt` (`platformLocalModule`). The web `platformLocalModule` is empty: a local data source that the app needs on the web gets an in-memory implementation registered in `LocalModule.web.kt`, behind the same contract. Room's generated code comes from KSP per target (`kspAndroid`, `kspJvm`, `kspIosArm64`, `kspIosSimulatorArm64`); the database uses `BundledSQLiteDriver`.

## DTO → Domain mapping

Mandatory rules for `Network*` → Domain entity mappers:

1. **NEVER use `?: ""`, `?: false`, `?: 0`, `.orEmpty()`** to mask a nullable response field. If the backend does not send a field, the domain model reflects that absence with Kotlin's nullable type.
   - **Only exception — lists whose absence means "empty" by contract**: `.orEmpty()` on a **list** is allowed when the backend not sending it means, by contract, zero elements and not unknown data. Each element's fields still follow rules 2–5. If the list's absence could mean something else (partial error, field not supported by that backend version), the exception does not apply. Justify the case in the PR with the contract or the real request that backs it.
2. **Return nullable** (`-> DomainEntity?`) when the mapping can fail because critical data is missing (e.g. `id`).
3. **Short-circuit on critical fields** (`id`, fields required by business logic) with an early `return null` if they are missing, instead of letting an invalid value through.
4. **Carry nullability over to the domain model**: fields the backend may not send are `String?`/`Int?`/etc. in the entity, not defaulted to empty.
5. **For lists, use `.mapNotNull`** (not `.map`) to filter out entities that could not be mapped.

**⚠️ MANDATORY**: deciding which fields are required (non-nullable) vs. optional is a business decision. ALWAYS ask the user explicitly before defining it, after verifying the real contract (a real request to the backend, not just what the docs or the ticket say).

```kotlin
// ✅ Correct
internal fun NetworkThing.toThing(): Thing? {
    val id = id ?: return null
    return Thing(
        id = id,
        name = name,           // String? → String? (no ?:)
        imageUrl = imageUrl,   // String? → String? (no ?:)
    )
}

// ❌ Wrong
internal fun NetworkThing.toThing(): Thing =
    Thing(
        id = id ?: "",         // ← masks missing data
        name = name ?: "",     // ← the UI shows an empty text without knowing the data is missing
    )
```

Why: silent defaults cause downstream bugs (sending an empty id to an endpoint, blank cards in the UI without knowing data is missing). If the data does not come, the nullable travels up to the layer that decides what to show.

**About `NetworkTodo`**: the sample DTO declares all its fields non-nullable because DummyJSON always sends them. With kotlinx.serialization, a non-nullable field missing from the JSON throws `MissingFieldException` and the whole response fails (`Error.JsonSyntax`). Do not copy it as a template for a real backend: decide nullability field by field from the contract.

## Backend → Domain enum mapping

**⚠️ MANDATORY**: when a response field contains enum-like values (strings that represent categories, types, states), the type is **never** a Kotlin `enum class` decoded directly by kotlinx.serialization. The generated serializer throws `SerializationException` as soon as the backend sends an unknown value: there is no fallback, and parsing of the whole object fails.

Valid patterns:

**A — sealed class + manual `valueOf` with fallback** (recommended by default: the field arrives as a raw `String` in the DTO):

```kotlin
// Domain — domain:model
sealed class Status(val value: String) {
    data object Unknown : Status(UNKNOWN)
    data object Pending : Status(PENDING)
    // ...
    companion object {
        fun valueOf(value: String): Status = when (value.uppercase()) {
            PENDING -> Pending
            // ...
            else -> Unknown
        }
    }
}

// Data — data:sourcesImpl:remote
private fun NetworkThing.mapStatus() = status?.let { Status.valueOf(it) } ?: Status.Unknown
```

**B — `enum class` in the DTO + custom `KSerializer` with fallback**, when the DTO needs strong typing during parsing itself:

```kotlin
@Serializable(with = NetworkPromotionTypeSerializer::class)
internal enum class NetworkPromotionType { AMOUNT, PERCENT, UNKNOWN }

internal object NetworkPromotionTypeSerializer : KSerializer<NetworkPromotionType> {
    override val descriptor = PrimitiveSerialDescriptor("NetworkPromotionType", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): NetworkPromotionType =
        decoder.decodeString().uppercase().let { value ->
            NetworkPromotionType.entries.firstOrNull { it.name == value } ?: NetworkPromotionType.UNKNOWN
        }
    override fun serialize(encoder: Encoder, value: NetworkPromotionType) = encoder.encodeString(value.name)
}
```

Without `with =`, the `enum class` is left exposed with no safety net.

Why: both patterns are type-safe, resilient to new backend values (they fall into `Unknown`/`UNKNOWN` instead of breaking) and keep traceability with the real API contract.

## Tests

- Repositories and use cases: hand-written fakes of the contracts, in `commonTest`.
- The remote stack: integration tests against the real `remoteModule` with `HttpClientEngine` replaced by `MockEngine` in a `koinApplication { }` (`TodoRemoteDataSourceIntegrationTest`): status codes, error bodies, malformed JSON and transport failures. They run on every target.
