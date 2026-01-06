# Approach to Testing

## Overview
- Test framework: JUnit 5 with Quarkus integration via `@QuarkusTest` for server-side/integration tests.
- HTTP testing: RestAssured + small request/response helpers live in `src/test/kotlin/com/aiskov/RestAssuredHelper.kt`.
- DB access in tests: tests call into the application's `DbCollectionProvider` (a CDI bean that produces a 
  `com.mongodb.client.MongoClient`) and use small DB helper utilities under `src/test/kotlin/com/aiskov/utils`.
- Tests are integration-style (they run inside Quarkus JVM and exercise HTTP endpoints and database operations). 
  The project currently talks to a real MongoDB instance configured via `application.properties`.

## Tools
- JUnit 5 - test framework
- Quarkus Test Framework - for booting the application in test mode
  - MongoDB configured via `application.properties` for tests
- RestAssured - for HTTP endpoint testing
  - Hamcrest - for response assertions
- Kotlin Faker - for generating test data
- AssertJ - for fluent assertions of DB state

## Key files and utilities
- `src/test/kotlin/com/aiskov/RestAssuredHelper.kt`
  - Helpers: `jsonBody(body)`, `post(url, body)`, and `ValidatableResponse.commandResponse(id, version)` to assert common response shapes.

- `src/main/kotlin/com/aiskov/utils/DbHelper.kt`
  - `doc(...)`, `byId(id)`, `any()` produce `org.bson.Document` instances used in queries.
  - `MongoCollection<T>.findById(id)` convenience to return the first matching document.

- `src/test/kotlin/com/aiskov/utils/DbHelper.kt`
  - `DbCollectionProvider.ensureExists(entry)` helper used by tests to insert fixtures directly into collections.

- `src/test/kotlin/com/aiskov/utils/*Data.kt` (fixtures)
  - Reusable fixture data for `User` and command payloads (used across tests).

## Test structure and patterns
- Tests are annotated with `@QuarkusTest`, which boots the Quarkus test runtime and provides CDI injection into 
  test classes.
- Each test class typically injects `DbCollectionProvider` and cleans state in a `@BeforeEach` by deleting documents 
  in the relevant collection:
  - Example: `dbProvider.collection(User::class).deleteMany(any())`
- Tests exercise the application through HTTP endpoints using functions like `post(...)` from `RestAssuredHelper`, 
  then assert both HTTP response and DB state.
- To seed data tests use `dbProvider.ensureExists(...)` which inserts a document into the collection and returns
  the inserted entry.
- Assertions on responses often use `commandResponse(id, version)` helper for the common command API response format 
  as well as Hamcrest matchers for error responses.

## Best practices for writing tests in this repo
1. Isolation
   - Clean collection state before each test (`deleteMany(any())`) to avoid cross-test interference.
   - Prefer inserting only the minimal fixtures needed for a test.

2. Deterministic fixtures
   - Use `UserData` fixtures and `ensureExists(...)` helper to make fixtures readable and reusable.
   - Avoid random data generation unless deterministically seeded.

3. Use the HTTP helpers
   - Use `post(url, payload)` and `jsonBody(payload)` so tests are consistent and concise when exercising REST endpoints.

4. DB types for Pojo fields
   - Store arbitrary JSON content as `Document`. If you need a Kotlin `Map` in the public API, convert on read/write 
     via small helpers (Document ↔ Map).

5. Codec & constructor pitfalls
   - If you annotate constructors with `@BsonCreator`, do so on regular Kotlin classes, not data classes. If you need 
     Kotlin data class semantics, consider a custom codec/provider that uses Kotlin reflection to honor default 
     parameter values.

## How to write a new test (template)
1. Create test class annotated with `@QuarkusTest`.
2. Inject `DbCollectionProvider`.
3. Clean up collection in `@BeforeEach`.
4. Prepare payload (use `UserData` fixture or build a DTO).
5. Call the API via `post("/api/...", payload)`.
6. Assert response status and body with helpers (`commandResponse`) and Hamcrest matchers.
7. Query DB with `dbProvider.collection(MyAggregate::class).findById(id)` and assert persisted state.

Example: `kotlin/com/aiskov/domain/user/command/CreateUserV1CommandTest.kt`
