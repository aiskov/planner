# Approach

## Multithreading
Application is designed to be thread-safe, allowing concurrent access to resources without data corruption
or inconsistency.

Application should use reactive approach and kotlin async/await where possible to improve performance and responsiveness.

## Error handling
Application shouldn't use exceptions for flow control. Instead, it should use result types or similar constructs to 
represent success and failure states.

## Structure
All elements are located in parent package com.aiskov.time.tracker.

- .config - Configuration classes and settings.
- .modules - Application modules.
  - .common - Common classes and utilities used across modules.
  - .<module-name> - Each application module
    - .commands - Command files. 
    - .query - Query files.
    - .values - Value objects and enums.
    - .aggregates - Aggregate roots and domain entities.
    - .*Service.kt - Service classes containing business logic.
    - .*QueryRepository.kt - Query repository interfaces for data retrieval.
    - .*Policies.kt - Domain policies and rules. Allows to aggregate apply verifications and modifications that requires
      access to Repositories and Services. Also could be used to extract complex or repeatable logic from aggregates.

## Persistency
- Each aggregate should be saved as separate document in the database.
- Use optimistic locking for concurrent updates.
- Command repository should allow to save/update aggregate, get aggregate by id.
  - All update operations should check version for optimistic locking.
- Query repository should return use mongodb aggregation framework to get all data required for the case.
  - Methods should return query Result DTOs that contains only data required for the case.

### Query Repository
#### Find by filter methods
Contract:
```kotlin
fun <T> findByFilter(type: KClass<T>, filter: AggregateFilters): List<T>
```

Usually we will have at least two Response DTOs:
- `*ListQueryResponse` - describes fields of list response
- `*DetailsQueryResponse` - describes fields of find by id response

Potentially they could have different fields. So when we build MongoDB aggregation pipeline we should conditionally add
stages to project required fields, sort, skip, limit, lookups etc.

We should always have filter by ids in order to process with same method both list and details queries.

#### Other methods
Contract:
```kotlin
fun isExists(params...): Boolean
fun count(params...): Int
```

That methods should be used mainly in policies to verify some conditions or by services to get counts for pagination.

## Optimistic Locking
Aggregate contains version field of type Int. When aggregate is created, version is set to 1. All update operations
should provide expected version. If expected version doesn't match current version, update should fail with 
ConcurrentChangeException.

Expected version is received from client in Request/Command DTOs. That version should be provided to UI in each 
Response DTOs.

Command processing should apply two verifications:
- Check that expected version matches current aggregate version in CommandGateway after fetch aggregate from database.
- Add version to the update query condition in CommandRepository. As it is second layer it could fail with generic 
  database exception.

## Concurrency model (recommended)
- Primary model: Kotlin coroutines (suspend functions, structured concurrency and Kotlin Flow) are the preferred 
  concurrency primitives for application code. (Fix: "async/away" → "async/await" / coroutines semantics.)
- Use suspend functions and structured concurrency for business logic. Prefer coroutine-friendly libraries where possible.
- If a blocking library is used, run blocking calls on `Dispatchers.IO` to avoid blocking Quarkus event-loop threads.

Concrete thread-safety rules
- Confine mutable state to single-threaded contexts or protect it with proper synchronization.
- Prefer immutable data objects and pure functions where practical.
  - Aggregates should stay mutable, but avoid sharing mutable aggregate instances across coroutines/threads.
- Do not block the Quarkus event-loop threads; offload long-running or blocking operations to `Dispatchers.IO` or 
  annotate REST handlers with `@Blocking` when appropriate.

Thread-safety rules — coroutines vs reactive code
- Coroutines (recommended)
  - Use structured concurrency and `suspend` functions for business logic.
  - Avoid shared mutable state across coroutines. When mutable state is required, confine it to a single 
    coroutine/Dispatcher or use `kotlinx.coroutines.sync.Mutex` or an actor (`Channel`) pattern to serialize access.
  - Use `withContext(dispatchers.io)` (or `Dispatchers.IO`) to run blocking calls off the main/default dispatcher. 

Rules summary
- Single preferred model per layer: coroutines for service/business logic; reactive for low-level non-blocking plumbing only when required.
- Always document conversion points and ensure tests exercise the boundary behavior.

## Command Structure
- Command is a validated DTO that represents an action to be performed on the system and contains necessary data.
- In order to have in command non-nullable fields (if they are logically required) used Request DTOs that should have
  same fields and validation rules. Request DTO fields should be nullable for correct deserialization.
- Request should implement toCommand() method that converts it to Command. Expected that method are called after
  validation. By this reason it could simply force conversion to non-nullable fields.
- Enums in Request should be presented as strings. But toCommand() method should convert them to enum values.
- Endpoint created by command resource method. Command resource created per command.
- Command resource only transfer request DTO to CommandGateway and return response DTO.
- Command should be registered in CommandHandlerConfig, that describes which aggregate method should be called to
  process the command.
- Each command file should contain single command class, resource class and request class.
- Request DTO should be documented using appropriate swagger annotations.
- Commands should be versioned using name and path used in resource.

## Query Structure
- Query is DTO that contains parameters for data retrieval.
- Response DTO is used to return data to client.
- Query and Response DTOs should be documented using appropriate swagger annotations.
- Query file should contains single query class, query resource class and response class.
- Query resource should call module service that is responsible for query processing.
- Service should call QueryRepository to get data from database.
