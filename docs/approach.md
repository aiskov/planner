# Approach

## Multithreading
Application is designed to be thread-safe, allowing concurrent access to resources without data corruption
or inconsistency.

Application should use reactive approach and kotlin async/away where possible to improve performance and responsiveness.

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