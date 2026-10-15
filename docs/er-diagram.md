# Entity Relationship (ER) Diagram - Online Project Management Tool

## Database Name: `project_management_db`

The database schema is fully normalized up to 3NF (Third Normal Form) with primary keys, foreign keys, cascade rules, unique constraints, and optimized indexes.

```mermaid
erDiagram
    USERS ||--o{ PROJECTS : "manages"
    USERS ||--o{ PROJECT_MEMBERS : "participates in"
    PROJECTS ||--o{ PROJECT_MEMBERS : "includes"
    PROJECTS ||--o{ TASKS : "contains"
    USERS ||--o{ TASKS : "assigned to"
    TASKS ||--o{ TASK_COMMENTS : "has"
    USERS ||--o{ TASK_COMMENTS : "authors"
    USERS ||--o{ ACTIVITY_LOGS : "performs"
    PROJECTS ||--o{ ACTIVITY_LOGS : "scoped to"
    TASKS ||--o{ ACTIVITY_LOGS : "scoped to"
    USERS ||--o{ NOTIFICATIONS : "receives"

    USERS {
        bigint id PK
        varchar(100) name
        varchar(150) email UK
        varchar(255) password_hash
        enum role "ADMIN, PROJECT_MANAGER, TEAM_MEMBER"
        enum account_status "ACTIVE, INACTIVE"
        timestamp created_at
        timestamp updated_at
    }

    PROJECTS {
        bigint id PK
        varchar(200) title
        text description
        date start_date
        date due_date
        enum status "PLANNING, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED"
        bigint manager_id FK
        timestamp created_at
        timestamp updated_at
    }

    PROJECT_MEMBERS {
        bigint id PK
        bigint project_id FK
        bigint user_id FK
        timestamp joined_at
    }

    TASKS {
        bigint id PK
        bigint project_id FK
        varchar(255) title
        text description
        bigint assigned_to FK
        enum priority "LOW, MEDIUM, HIGH, CRITICAL"
        enum status "TODO, IN_PROGRESS, IN_REVIEW, COMPLETED"
        date start_date
        date due_date
        timestamp created_at
        timestamp updated_at
    }

    TASK_COMMENTS {
        bigint id PK
        bigint task_id FK
        bigint user_id FK
        text comment
        timestamp created_at
    }

    ACTIVITY_LOGS {
        bigint id PK
        bigint user_id FK
        bigint project_id FK
        bigint task_id FK
        varchar(100) action_type
        varchar(500) description
        timestamp created_at
    }

    SYSTEM_SETTINGS {
        bigint id PK
        varchar(100) setting_key UK
        text setting_value
        timestamp updated_at
    }

    NOTIFICATIONS {
        bigint id PK
        bigint user_id FK
        varchar(500) message
        varchar(50) notification_type
        boolean is_read
        timestamp created_at
    }
```

## Table Relationships & Cardinalities

| Entity 1 | Relationship | Entity 2 | Cardinality | Foreign Key Constraint & Deletion Rule |
| :--- | :---: | :--- | :---: | :--- |
| `users` | Manages | `projects` | `1 : N` | `manager_id` -> `users(id)` (`ON DELETE RESTRICT`) |
| `projects` | Maps to | `project_members` | `1 : N` | `project_id` -> `projects(id)` (`ON DELETE CASCADE`) |
| `users` | Maps to | `project_members` | `1 : N` | `user_id` -> `users(id)` (`ON DELETE CASCADE`) |
| `projects` | Contains | `tasks` | `1 : N` | `project_id` -> `projects(id)` (`ON DELETE CASCADE`) |
| `users` | Assigned to | `tasks` | `1 : N` | `assigned_to` -> `users(id)` (`ON DELETE SET NULL`) |
| `tasks` | Has | `task_comments` | `1 : N` | `task_id` -> `tasks(id)` (`ON DELETE CASCADE`) |
| `users` | Authors | `task_comments` | `1 : N` | `user_id` -> `users(id)` (`ON DELETE CASCADE`) |
| `users` | Generates | `activity_logs` | `1 : N` | `user_id` -> `users(id)` (`ON DELETE CASCADE`) |
| `users` | Receives | `notifications` | `1 : N` | `user_id` -> `users(id)` (`ON DELETE CASCADE`) |

## Integrity & Indexing Strategy
- **Unique Constraints:**
  - `users(email)`: Ensures no duplicate user registration.
  - `project_members(project_id, user_id)`: Prevents duplicate membership assignments.
  - `system_settings(setting_key)`: Unique configuration keys.
- **Indexes:**
  - `idx_users_role`, `idx_users_status`, `idx_users_email`
  - `idx_projects_manager`, `idx_projects_status`, `idx_projects_dates`
  - `idx_tasks_project`, `idx_tasks_assigned`, `idx_tasks_status`, `idx_tasks_priority`, `idx_tasks_due_date`
  - `idx_comments_task`, `idx_logs_user`, `idx_notifications_user`
