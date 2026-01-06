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
    - .api - API endpoints (Resources).
      - .commands - Command DTOs
      - .responses - Response DTOs
      - .*Resource.kt - Resource classes defining API endpoints
    - .values - Value objects and enums.
    - .aggregates - Aggregate roots and domain entities.
    - .*Service.kt - Service classes containing business logic.
    - .*CommandRepository.kt - Command repository interfaces for data access.
    - .*QueryRepository.kt - Query repository interfaces for data retrieval.

## Persistency
- Each aggregate should be saved as separate document in the database.
- Use optimistic locking for concurrent updates.
- Command repository should allow to save/update aggregate, get aggregate by id.
- Query repository should return use mongodb aggregation framework to get all data required for the 

## Optimistic Locking
Each aggregate should have version field that is incremented on each update. When updating aggregate.
Each command that modify aggregate should provide expected version. If expected version doesn't match current version,
update should fail with ConcurrentChangeException.

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
