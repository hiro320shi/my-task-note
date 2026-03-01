# MyTaskNote API

タスク管理アプリ「MyTaskNote」のバックエンドAPIです。  
ユーザー登録 → ログイン（JWT）→ タスクCRUD を提供します。

---

## Tech Stack
- Java 17 / Spring Boot
- Spring Web / Validation
- Spring Security（JWT）
- MySQL 8.0（Docker）
- JPA (Hibernate)
- Gradle / JaCoCo

---

## Requirements
- Java 17+
- Docker Desktop
- (任意) Postman

---

## Quick Start（ローカル起動）

### 1) DB起動（Docker）
`backend/api` 配下で実行します。

```bash
docker compose up -d
docker compose ps