# ✅ 1週目チェックリスト（準備 & 設計）（9/1～9/7）

## Day 1：GitHub & VS Code 環境準備
- [X] GitHub アカウント作成（未作成の場合）
- [X] 新規リポジトリ作成（Private 推奨）
- [X] ローカル PC にクローン
- [X] Git 設定（ユーザ名・メールアドレス）
- [X] VS Code インストール
- [X] 拡張機能インストール（vscode_extensions.md参照）
- [X] プロジェクト用フォルダ構成作成（`frontend/`, `backend/`, `docs/`）

---

## Day 2：Docker & AWS CLI 環境準備
- [X] Docker Desktop インストール
- [X] `docker run hello-world` で動作確認
- [X] Docker Compose インストール（必要に応じて）
- [X] AWS CLI インストール
- [X] `aws configure` でアクセスキー・シークレットキー設定
- [X] `aws s3 ls` で接続確認

---

## Day 3：SAM CLI & Lambda ローカル動作確認
- [X] SAM CLI インストール
- [X] バージョン確認 (`sam --version`)
- [X] Hello World テンプレートで Lambda デプロイ確認
- [X] ローカルで Lambda テスト実行
- [X] Lambda → Aurora 接続方式を調査（構想レベル）

---

## Day 4：全体アーキテクチャ設計
- [X] アーキテクチャ図作成（フロント・バックエンド・DB・パラメータストア・S3/CloudFront）
- [X] API設計（ユーザ登録/ログイン/プロフィール取得）
- [X] API設計（タスク CRUD）
- [X] エンドポイント一覧表作成（メソッド・URL・リクエスト/レスポンス）

---

## Day 5：フロント画面設計
- [X] ページ一覧作成（ログイン、タスク一覧、タスク登録/編集）
- [X] 各画面のワイヤーフレーム作成（紙・Figma・Miroなど）
- [X] 各画面で必要なデータ整理（APIとの対応付け）

---

## Day 6：DB設計
- [X] ユーザテーブル設計  
  - id, name, email, password_hash, created_at, updated_at  
- [X] タスクテーブル設計  
  - id, user_id, title, description, status, created_at, updated_at  
- [X] ER図作成（ユーザ ↔ タスク）
- [X] 初期データ / シードデータ設計

---

## Day 7：開発ルール・ドキュメント整理
- [X] Git ブランチ戦略決定（main / develop / feature/*）
- [X] コーディング規約・コミットメッセージ規則決定
- [X] README.md 作成
- [X] 設計メモ・ER図・API仕様書を `docs/` にまとめる
- [X] 1週目振り返り、2週目バックエンド開発の準備
