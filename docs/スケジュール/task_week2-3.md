# 2〜3週目：バックエンド開発タスク（1日単位チェックリスト）（9/8～9/21）

## 1日目：プロジェクト準備と環境構築
- [X] Spring Boot プロジェクト作成
- [X] 必要な依存ライブラリ追加（Spring Web, Spring Data JPA, Spring Security 等）
- [X] ローカル開発用の Docker コンテナ作成（MySQL / Aurora互換）
- [X] Git リポジトリに初回コミット

## 2日目：DB接続設定
- [X] Secrets Manager で DB のシークレット作成
- [X] Spring Boot で Secrets Manager からの接続情報取得設定
- [X] JPA エンティティ作成（User, Task）
- [X] Docker 上で DB 接続テスト

## 3日目：ユーザ登録 API 作成
- [X] ユーザ登録用 DTO 作成
- [X] ユーザ登録 API 実装（POST /users）
- [X] 入力バリデーション実装
- [X] 単体テスト作成（ユーザ登録成功・失敗ケース）

## 4日目：ユーザ認証 / ログイン API 作成
- [X] Spring Security 設定（JWT認証）
- [X] ログイン API 実装（POST /login）
- [X] JWT 発行・検証処理実装
- [X] 単体テスト作成（ログイン成功・失敗ケース）

## 5日目：タスクCRUD（Create/Read）
- [X] タスク登録 API 実装（POST /tasks）
- [X] タスク一覧取得 API 実装（GET /tasks）
- [X] 単体テスト作成（タスク登録・取得）

## 6日目：タスクCRUD（Update/Delete）
- [X] タスク更新 API 実装（PUT /tasks/{id}）
- [X] タスク削除 API 実装（DELETE /tasks/{id}）
- [X] 単体テスト作成（タスク更新・削除）

## 7日目：セキュリティテスト & 結合確認
- [X] 認証ありAPIのアクセス制御テスト
- [X] ユーザ権限によるアクセス制御確認
- [X] Docker コンテナ上での総合動作確認

## 8日目：コード整理とリファクタリング
- [X] コードのリファクタリング（可読性・保守性向上）
- [X] 不要なコメント・ログ削除
- [X] パッケージ構成の見直し

## 9日目：単体テスト拡充
- [X] 境界値テスト追加
- [X] 異常系テスト追加
- [X] テストカバレッジ確認

## 10日目：最終確認 & ドキュメント作成
- [X] Postman で API 動作確認
- [X] README に API 仕様・Docker 起動手順を記載
- [X] Git に最終コミット・プッシュ