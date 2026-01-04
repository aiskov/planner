# User
Base entity of user management.

## Model
- id: Unique identifier for the user, must be a valid email address.
    - type: string
    - required: true
    - format: email
    - length: 5-100
- name: Human readable name of the user, shown in the UI.
    - type: string/single-line
    - required: true
    - length: 3-100
- password: Password of the user.
    - type: string
    - required: true
    - length: 8-60
    - format: allow alphanumeric and special characters available on standard keyboards
    - requirements: at least one uppercase letter, one lowercase letter, one digit, and one special character
    - store: hashed
- config: User-specific configuration settings.
    - type: Map<String, Object>
    - required: false
- isAdmin: Flag indicating if the user has administrative privileges.
    - type: boolean
    - required: true
    - default: false

## Operations
- Commands:
  - CreateUser
    - Fields: id as email, name, password
    - Calculate:
      - assign default config 
  - UpdateUserDetails
    - Fields: name
  - ChangeUserPassword
    - Fields: currentPassword, newPassword
  - UpdateUserConfig
    - Fields: config
  - LoginUser
    - Fields: id as email, password
  - LogoutUser
    - Fields: id as email
  - DeleteUser
    - Fields: -

- Queries:
  - GetCurrent
    - Fields: id as email, name, config
  - GetById
    - Fields: id as email, name
  - GetAllUsers
    - Fields: id as email, name
    - Sorting: email (DEFAULT), name
    - Search term on: email, name
    - Filtering: -
