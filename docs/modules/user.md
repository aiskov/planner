# User
Base entity of user management.

Model:
- id: Unique identifier for the user, must be a valid email address.
    - type: string
    - required: true
    - format: email
    - length: 5-100
- name: Human readable name of the user, shown in the UI.
    - type: string
    - required: true
    - length: 3-100
- config: User-specific configuration settings.
    - type: Map<String, Object>
    - required: false