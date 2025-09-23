| メソッド   | URL                   | リクエストBody              | レスポンス例       | 認証 |
| ------ | --------------------- | ---------------------- | ------------ | -- |
| POST   | `/api/users/register` | email, password, name  | userId, name | 不要 |
| POST   | `/api/users/login`    | email, password        | JWTトークン      | 不要 |
| GET    | `/api/users/profile`  | -                      | user情報       | 要  |
| GET    | `/api/tasks`          | -                      | タスク一覧        | 要  |
| POST   | `/api/tasks`          | title, status, dueDate | 作成したタスク      | 要  |
| PUT    | `/api/tasks/{id}`     | title, status, dueDate | 更新後のタスク      | 要  |
| DELETE | `/api/tasks/{id}`     | -                      | 削除結果         | 要  |

#### ユーザー系API（認証・許可回り）
1. ユーザ登録  

リクエスト  
```
{
  "email": "test@example.com",
  "password": "password123",
  "name": "Taro"
}
```
レスポンス  
```
{
  "userId": "u12345",
  "email": "test@example.com",
  "name": "Taro"
}
```
2. ログイン(JWTを背発行)  

リクエスト  
```
{
  "email": "test@example.com",
  "password": "password123"
}  
```
レスポンス  
```
{
  "token": "jwt-token-string",
  "expiresIn": 3600
}
```
3. プロフィール取得

リクエストヘッダ
```
Authorization: Bearer <jwt-token-string>
```
レスポンス  
```
{
  "userId": "u12345",
  "email": "test@example.com",
  "name": "Taro"
}

```
#### タスク系API（CRUD）
1. タスク一覧  

レスポンス  
```
[
  {
    "taskId": "t1",
    "title": "買い物に行く",
    "status": "TODO",
    "dueDate": "2025-09-25"
  },
  {
    "taskId": "t2",
    "title": "資料作成",
    "status": "DONE",
    "dueDate": "2025-09-18"
  }
]
```
2. タスク作成

リクエスト  
```
{
  "title": "新しいタスク",
  "status": "TODO",
  "dueDate": "2025-09-30"
}
```
レスポンス  
```
{
  "taskId": "t3",
  "title": "新しいタスク",
  "status": "TODO",
  "dueDate": "2025-09-30"
}
```
3. タスク更新

リクエスト
```
{
  "title": "タイトル更新",
  "status": "IN_PROGRESS",
  "dueDate": "2025-09-28"
}
```
レスポンス  
```
{
  "taskId": "t3",
  "title": "タイトル更新",
  "status": "IN_PROGRESS",
  "dueDate": "2025-09-28"
}
```
4. タスク削除

レスポンス  
```
{
  "message": "Task deleted"
}
```