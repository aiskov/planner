ToDo
====

This file lists the remaining work required to bring the implementation to match project documentation.

Files:
- docs/requirements.md
- docs/approach.md
- docs/test.md
- docs/modules/*.md

Status legend
- Done: implemented and present in codebase
- Partial: partially implemented or needs refinement
- Missing: not implemented
- Postponed: out of scope for now
- Ignored: not applicable

All tasks should follow this template:
```markdown
### 1 - Title of task
Status: [Done | Partial | Missing | Postponed | Ignored]
Reference: docs/some-file.md#some-header
Description: 
Brief description of the task. Potentially what is missing or what needs to be done.
Options:
- Some implementation option 1
- Some implementation option 2
```

## Tasks
### 3 - Normalize package and filename examples in `docs/approach.md`
Status: Ignored
Reference: docs/approach.md#structure
Description: Replace leading-dot package examples and mixed wildcard/file patterns with full package names (e.g., `com.aiskov.time.tracker.config`) and concrete filename patterns (`*Resource.kt`). Include 1-2 example paths.
Options:
- Use full package examples
- Add a small tree/example layout

### 4 - Complete Persistency section in `docs/approach.md`
Status: Partial
Reference: docs/approach.md#persistency
Description: Finish the truncated sentence about query repository expectations. Clarify what query methods should return (DTOs/Views), when MongoDB aggregation framework is required, and give one example aggregation use-case.
Options:
- Add example aggregation for joined data
- State performance/consistency considerations

### 7 - Clarify user context-selection behavior in `docs/requirements.md`
Status: Postponed
Reference: docs/requirements.md#users-management
Description: Explain how users select context (Work/Personal), whether selection is persistent per user or per session, and how it affects visible projects/tasks. Replace the gendered pronoun with neutral language.
Options:
- Persist context per user profile
- Make selection session-scoped by default

### 8 - Define the permission model in `docs/requirements.md`
Status: Postponed
Reference: docs/requirements.md#users-management
Description: Describe role hierarchy (Admin, Full-access, Read-only), how project-scoped permissions interact with organization roles, and conflict resolution rules (precedence, overrides).
Options:
- Org-level role overrides project roles
- Project-level explicit grants override org default

### 10 - Finish Epic description in `docs/requirements.md`
Status: Postponed
Reference: docs/requirements.md#tasks-management
Description: Complete the epic bullet (what an Epic contains—Stories/Tasks), and clarify how estimations propagate (epic points vs story points) and aggregation rules.
Options:
- Define containment model (Epic -> Stories -> Tasks)
- Specify estimation roll-up rules

### 11 - Specify recurrence semantics for tasks in `docs/requirements.md`
Status: Missing
Reference: docs/requirements.md#tasks-management
Description: Define recurrence types (daily/weekly/custom), whether completed instance history is preserved, how deadlines are calculated, and how checklists/subtasks are handled on recurrence.
Options:
- Preserve history and create a new task instance on recurrence
- Reuse same task ID and version history (with occurrence metadata)

### 12 - Clarify checklist item states in `docs/requirements.md`
Status: Missing
Reference: docs/requirements.md#checklists
Description: Define allowed checklist item states (Pending / Done / Canceled), how canceled differs from done for reporting/metrics, and whether canceled items can be reopened.
Options:
- Treat Canceled as not-done for metrics
- Treat Canceled as completed with separate tag

### 13 - Define notes expiration semantics in `docs/requirements.md`
Status: Missing
Reference: docs/requirements.md#notes
Description: Specify what "expiration date" means for notes (auto-archive, hide, or delete), default retention policy, and whether expiry triggers notifications or exports.
Options:
- Auto-archive after expiration (recommended)
- Auto-delete after grace period

### 14 - Fix typo 'Store date in MongoDB' in `docs/requirements.md`
Status: Missing
Reference: docs/requirements.md#non-functional-requirements
Description: Correct to "Store data in MongoDB" and add short notes on basic data modeling conventions (date/time format, object IDs, index guidance).
Options:
- Minor text fix only
- Add a short subsection with modeling examples

### 15 - Clarify testing boundaries in `docs/requirements.md`
Status: Missing
Reference: docs/requirements.md#testing
Description: Explain why domain logic uses integration tests, define clear boundaries for unit vs integration tests, and list examples of what to test in each category.
Options:
- Keep domain tests as integration tests only (with rationale)
- Allow unit tests for pure domain logic where possible

### 16 - Specify documentation format for endpoints in `docs/requirements.md`
Status: Missing
Reference: docs/requirements.md#documentation
Description: Decide whether endpoints are documented via OpenAPI (generated) or manual markdown and provide the desired file/layout conventions and example for one resource.
Options:
- Use OpenAPI for machine-readable schemas and embed examples in docs/
- Use hand-written markdown per endpoint (specify template)

### 17 - Reconcile test DB setup in `docs/test.md`
Status: Missing
Reference: docs/test.md#tools
Description: Clarify whether tests should use Testcontainers in CI or a real MongoDB. Provide recommended local and CI configurations and a short migration plan for test setup.
Options:
- Use Testcontainers in CI and local dev
- Allow local real MongoDB via profile but require Testcontainers in CI

### 18 - Add test `application.properties` and Testcontainers snippet to `docs/test.md`
Status: Missing
Reference: docs/test.md#tools
Description: Add minimal `application.properties` for tests showing MongoDB connection properties, and a concise Testcontainers setup snippet used in Quarkus tests.
Options:
- Show example properties and a Kotlin/Testcontainers snippet
- Point to an existing test helper class with sample usage

### 19 - Clarify `DbHelper` responsibilities (main vs test) in `docs/test.md`
Status: Missing
Reference: docs/test.md#db-helpers
Description: Explain the difference between `src/main/.../DbHelper.kt` and `src/test/.../DbHelper.kt`, describe intended responsibilities, and recommend renaming or consolidation if appropriate.
Options:
- Keep both with clear naming (e.g., ProductionDbHelper vs TestDbHelper)
- Consolidate shared helpers into a single module with test-only helpers separate

### 20 - Add guidance for Document ↔ POJO mapping in `docs/test.md`
Status: Missing
Reference: docs/test.md#db-types-for-pojo-fields
Description: Provide examples for Document ↔ Kotlin object conversion, handling nested typed objects, and a strategy for schema evolution (backwards-compatible changes).
Options:
- Show small helpers for conversion
- Recommend using explicit DTOs for public APIs

### 21 - Add BSON codec example and guidance in `docs/test.md`
Status: Missing
Reference: docs/test.md#codec-constructor-pitfalls
Description: Add a short code example demonstrating recommended BSON codec usage for Kotlin classes and explain why annotating data class constructors with `@BsonCreator` is discouraged for certain patterns.
Options:
- Provide a minimal codec implementation sample
- Provide link to an example project or Quarkus config

### 22 - Simplify the `docs/todo.md` template and provide an example task
Status: Missing
Reference: docs/todo.md#template
Description: The current template is verbose; provide a minimal example task filled in using the template to guide contributors and clarify expected Status values.
Options:
- Replace template with a minimal example plus advanced template below
- Keep the current template and add a single filled example

### 23 - Add content to `docs/modules/context.md`
Status: Missing
Reference: docs/modules/context.md
Description: Create documentation for the Context module: data model, operations, relation to projects and tasks, and a short example workflow (create context, assign project).
Options:
- Draft a concise module doc with examples
- Link to domain model files and sample API endpoints

### 24 - Clarify `user.id` identity model in `docs/modules/user.md`
Status: Missing
Reference: docs/modules/user.md#user
Description: State whether `user.id` is the user's email (mutable) or an immutable UUID and describe the email-change flow and implications for external integrations.
Options:
- Use immutable UUID as primary id, keep email as separate field
- Use email as canonical id and document change/reset process

### 25 - Specify password storage and reset policy in `docs/modules/user.md`
Status: Missing
Reference: docs/modules/user.md#password
Description: Define accepted hashing algorithm (e.g., bcrypt), parameters, salting and iteration strategy, and briefly describe password reset flow and strength validation rules.
Options:
- Recommend bcrypt with configured cost parameter
- Provide link to `PasswordHasher.kt` implementation and required config

### 26 - Define `user.config` schema constraints in `docs/modules/user.md`
Status: Missing
Reference: docs/modules/user.md#config
Description: Constrain the `Map<String, Object>` into an allowed key set or JSON schema, and describe validation and migration rules for config changes.
Options:
- Define a JSON schema or typed config model
- Keep flexible map but document allowed keys and validation points

### 27 - Clarify `CreateUser` command behavior in `docs/modules/user.md`
Status: Missing
Reference: docs/modules/user.md#commands
Description: Describe which defaults are applied during user creation, source of defaults (system vs org), idempotency behavior, and required fields.
Options:
- Make CreateUser idempotent via client-supplied idempotency key
- Require email uniqueness and return existing user on duplicate

### 28 - Define `DeleteUser` semantics in `docs/modules/user.md`
Status: Missing
Reference: docs/modules/user.md#commands
Description: Specify whether delete is soft/hard, cascade rules for related entities (projects, time records), and required authorization/audit trails.
Options:
- Use soft-delete with reassign or archive of user's resources
- Implement hard-delete only for GDPR/cleanup with admin approval

### 29 - Expand `GetCurrent` query spec in `docs/modules/user.md`
Status: Missing
Reference: docs/modules/user.md#queries
Description: Extend `GetCurrent` response to include roles, organizations, last-seen timestamp, and privacy rules showing which fields are omitted or masked.
Options:
- Include roles and organization membership by default
- Add a minimal response example JSON

### 30 - Review and mark completed tasks as appropriate
Status: Missing
Reference: docs/todo.md
Description: Go through the codebase and close tasks that are already implemented or partially implemented; update Status accordingly in this file.
Options:
- Run a code-to-docs mapping pass and mark items Done/Partial
- Open issues/PRs for each Missing task
