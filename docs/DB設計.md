## ユーザテーブル設計
| カラム名           | 型                      | 制約                                                      | 説明             |
| -------------- | ---------------------- | ------------------------------------------------------- | -------------- |
| id             | BIGINT AUTO\_INCREMENT | PK                                                      | ユーザID（主キー）     |
| name           | VARCHAR(100)           | NOT NULL                                                | ユーザ名           |
| email          | VARCHAR(255)           | NOT NULL, UNIQUE                                        | メールアドレス（ログイン用） |
| password\_hash | VARCHAR(255)           | NOT NULL                                                | パスワード（ハッシュ済み）  |
| created\_at    | DATETIME               | DEFAULT CURRENT\_TIMESTAMP                              | 作成日時           |
| updated\_at    | DATETIME               | DEFAULT CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | 更新日時           |


## タスクテーブル設計
| カラム名        | 型                                  | 制約                                                      | 説明          |
| ----------- | ---------------------------------- | ------------------------------------------------------- | ----------- |
| id          | BIGINT AUTO\_INCREMENT             | PK                                                      | タスクID（主キー）  |
| user\_id    | BIGINT                             | FK → users.id                                           | ユーザID（外部キー） |
| title       | VARCHAR(200)                       | NOT NULL                                                | タスクタイトル     |
| description | TEXT                               | NULL可                                                   | タスク詳細       |
| status      | ENUM('TODO','IN\_PROGRESS','DONE') | DEFAULT 'TODO'                                          | ステータス       |
| created\_at | DATETIME                           | DEFAULT CURRENT\_TIMESTAMP                              | 作成日時        |
| updated\_at | DATETIME                           | DEFAULT CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | 更新日時        |
