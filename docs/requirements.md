# Requirements

## Functional Requirements
Application should provide next concepts:

- Users management
  - Users may be standalone or belong to organizations
  - Organizations may include multiple users
  - Users may have different roles in organizations: Admin, Full-access, Read-only
  - Full-access and Read-only access could be scoped by specific projects
  - User may select which in which context he will see tasks organization items
  - User may share his own items with other users
- Contexts management
  - User can have multiple contexts like: Work, Personal, Side Projects, etc.
  - Each context could contain projects, tasks, notes, and time records
- Projects management
  - Projects could contain tasks, checklists, notes, and time records
  - Projects could be assigned to 
- Tasks management
  - Tasks should have types
    - Simple - Basic type of task
      - May be assigned to Story (new simple tasks may be added to Story only if it's not done)
      - Estimated in hours
      - Simple tasks could have checklist attached
    - Bug - Task representing a bug in the system (behave same as Simple)
      - May be assigned to Story (new bugs may be added to Story even if it's marked as done)
      - Estimated in hours
    - Story - Task representing a user story
      - Estimated in story points
    - Epic - Large task that could contain 
      - Estimated in epic points
    - Routine - Task that used only for time tracking without completion
    - Placeholder - Task that used only to track some time window, and doesn't count as work done
  - Tasks should have deadlines
  - Tasks could be repeated it means that after completion they will be recreated with new deadline
  - Tasks should have priorities
  - Tasks should have statuses (e.g., To Do, In Progress, Done)
  - Tasks could have dependencies on other tasks
  - Tasks may be assigned to users
- Time tracking
  - Time tracking allows to create time records for tasks
  - Time records should have start and end time
- Checklists
  - Checklists contains items that be marked as done or canceled.
- Notes
  - Notes could contain markdown text
  - Notes could have expiration date
- Attachments
  - Attachments could be added to tasks, notes, and projects
- Comments
  - Comments could be added to tasks

## Non-Functional Requirements

### Technologies
- Expose Rest API
- Store date in MongoDB
- Backend written on Kotlin
- Backend framework: Quarkus

### Testing
- Application use only integration tests for domain logic verification
- Use Testcontainers for integration tests
- Achieve code coverage at least 80%
- Unit tests used for verification of utility functions only

### Structure
- DDD approach for domain modeling
- Separate command and query flows but keep them in the same package

### Documentation
- Approach is documentation first.
- All endpoints should be described in `docs/`

### Authentication
- Allow register users without oAuth
- Support for oAuth 2.0 authentication (Post MVP)

### Performance & Resource Usage
- Low latency for user interactions
- Low memory footprint

### Code
[approach.md](./approach.md)
