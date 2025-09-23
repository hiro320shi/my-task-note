```mermaid
erDiagram
    USERS ||--o{ TASKS : "has"

    USERS {
        BIGINT id
        VARCHAR name
        VARCHAR email
        VARCHAR password_hash
        DATETIME created_at
        DATETIME updated_at
    }

    TASKS {
        BIGINT id
        BIGINT user_id
        VARCHAR title
        TEXT description
        ENUM status
        DATETIME created_at
        DATETIME updated_at
    }
