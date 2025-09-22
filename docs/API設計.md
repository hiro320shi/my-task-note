| メソッド   | URL                   | リクエストBody              | レスポンス例       | 認証 |
| ------ | --------------------- | ---------------------- | ------------ | -- |
| POST   | `/api/users/register` | email, password, name  | userId, name | 不要 |
| POST   | `/api/users/login`    | email, password        | JWTトークン      | 不要 |
| GET    | `/api/users/profile`  | -                      | user情報       | 要  |
| GET    | `/api/tasks`          | -                      | タスク一覧        | 要  |
| POST   | `/api/tasks`          | title, status, dueDate | 作成したタスク      | 要  |
| PUT    | `/api/tasks/{id}`     | title, status, dueDate | 更新後のタスク      | 要  |
| DELETE | `/api/tasks/{id}`     | -                      | 削除結果         | 要  |
