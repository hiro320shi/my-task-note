# フロントエンド開発（5〜6週目）チェックリスト（9/29～10/12）
対象: My Task Note（Vue + Vite + Axios + Docker）

## 前提
- バックエンド(API Gateway + Lambda)は `/auth/*`, `/tasks/*` が稼働済み（ステージURL・APIキー等を控える）
- リポジトリ: `frontend/my-task-note-web`（例）
- Node.js 20 / pnpm または npm 使用（以下は pnpm 例）

---

## 5週目（実装フェーズ）

### Day 1: プロジェクト初期化 & 基盤整備
- [ ] Vite で Vue プロジェクト新規作成（TypeScript 有効）
- [ ] ESLint / Prettier / EditorConfig 導入 & フォーマットスクリプト追加
- [ ] Vite エイリアス `@/*` 設定（`tsconfig.json` / `vite.config.ts`）
- [ ] UI ライブラリ（任意: Element Plus / Vuetify など）選定 & インストール
- [ ] ディレクトリ構成雛形作成（`src/components`, `src/pages`, `src/services`, `src/store`, `src/types`）
- [ ] README にセットアップ手順を記載（Node 版、Docker 版）
- [ ] 最低限の起動確認（`pnpm dev`）

**成果物**
- [ ] 初期化済みのリポジトリ & CI フォーマットチェック（任意）

---

### Day 2: ルーティング & 認証ガード骨組み
- [ ] `vue-router` 導入（`/login`, `/tasks`, `/tasks/new` ルート）
- [ ] 未認証アクセス時のガード（`beforeEach`）実装（`/login` へリダイレクト）
- [ ] レイアウトコンポーネント（ヘッダー/フッター/コンテンツ枠）雛形
- [ ] 404 画面（NotFound）実装
- [ ] 簡易モックストア（認証状態の真偽値）でルーティング動作確認

**成果物**
- [ ] ルート遷移図（README または `/docs`）
- [ ] 404/ガード付きルーティングのスクリーンショット（任意）

---

### Day 3: 認証フロー実装（/auth/login, /auth/signup）
- [ ] Axios 基盤作成（`src/services/http.ts` にベースURL/タイムアウト/インターセプタ）
- [ ] ログイン画面 UI（ID/パスワード、バリデーション、送信ボタン）
- [ ] `/auth/login` 連携：成功時に JWT（例: `accessToken`）を保存（`localStorage` or `cookie`）
- [ ] サインアップ画面（任意） or リンクだけ用意
- [ ] エラートースト/メッセージ表示（401/400 対応）
- [ ] 認証状態をストア管理（Pinia or Vuex）。再読込時も状態復元

**成果物**
- [ ] 正常系ログイン後 `/tasks` に遷移するデモ動画（短尺）

---

### Day 4: タスク一覧ページ（/tasks）
- [ ] `/tasks` API 連携（一覧取得）
- [ ] テーブル表示（タイトル、期限、ステータス、更新日）
- [ ] ページング/ソート/簡易フィルタ（タイトル部分一致）
- [ ] ローディング/空データ/エラー表示の 3 状態 UI
- [ ] 行クリックで詳細（任意） or 編集画面への導線（以降の拡張余地）

**成果物**
- [ ] 一覧ページのスクリーンショット & 想定 API パラメータを README に追記

---

### Day 5: タスク登録ページ（/tasks/new）
- [ ] 登録フォーム UI（タイトル必須、説明、期限日、ステータス）
- [ ] フロント側バリデーション（必須/形式/文字数）
- [ ] `/tasks` POST 連携（201 成功時 `/tasks` へ遷移）
- [ ] 送信中ローディング、API エラー表示
- [ ] 成功トースト表示（UX）

**成果物**
- [ ] 正常登録 → 一覧反映のキャプチャ/GIF

---

## 6週目（品質向上 & Docker 化）

### Day 6: 共通部品 & DX 向上
- [ ] 共通フォーム部品（`<AppInput>`, `<AppSelect>`, `<AppDatePicker>` など）抽出
- [ ] バリデーションユーティリティ整備（`yup` or `vee-validate` など）
- [ ] エラーハンドリング規約（API 失敗時のトースト、再試行導線）を README へ
- [ ] Axios の 401 時リフレッシュ or ログアウト遷移（要件に合わせて）
- [ ] `.env`/`.env.local` 管理（API ベースURL, ステージ変数, API キーなど）

**成果物**
- [ ] 共通部品カタログ（Storybook 任意）

---

### Day 7: アクセシビリティ & i18n & テスト
- [ ] キーボード操作確認（フォーカスリング、フォームのエラーメッセージの関連付け）
- [ ] i18n 導入（最低限の文言辞書化、`ja`）
- [ ] ユニットテスト（`vitest`）でコンポーネント/サービスの基本ケース作成
- [ ] E2E（任意: Playwright）でログイン〜一覧〜登録のハッピーパス 1 本
- [ ] CI（任意: GitHub Actions）で lint/test 通す

**成果物**
- [ ] テスト実行ログ/バッジ（任意）

---

### Day 8: Docker 化（ローカル起動）
- [ ] `Dockerfile` 作成（ビルド段階 + 実行段階、`node:20` ベース）
- [ ] `docker-compose.yml` 作成（フロント単体／バックエンドと同一ネットワークの両想定）
- [ ] `VITE_*` 環境変数をコンテナに注入（`.env` と `--env-file` の扱い整理）
- [ ] コンテナ起動でホットリロード確認（`localhost:5173` など）
- [ ] イメージサイズ確認 & 最適化（`pnpm fetch --prod`/`--frozen-lockfile` など）

**成果物**
- [ ] `docker compose up` で起動する手順を README に追記

---

### Day 9: 本番ビルド & 配信検証
- [ ] `pnpm build` で `dist/` 生成
- [ ] コンテナで `nginx` 配信（任意: マルチステージ）
- [ ] キャッシュ戦略（`index.html` no-cache、アセットにハッシュ）確認
- [ ] 簡易負荷テスト（ローカル）
- [ ] 404 リダイレクト（SPA 用に `try_files` など）を検証

**成果物**
- [ ] 本番想定の Docker イメージ（タグ付け）

---

### Day 10: 仕上げ & 受け入れ
- [ ] README 最終整備（環境変数一覧、起動/テスト/ビルド/デプロイ手順）
- [ ] エラーパターンの最終確認（ネットワーク断/401/500）
- [ ] アクセシビリティ簡易監査（色コントラスト、フォームラベル）
- [ ] 既知の課題/次スプリント項目リストアップ
- [ ] ステークホルダーへデモ & フィードバック反映（小修正）

**成果物（Definition of Done）**
- [ ] ログイン→一覧→登録の基本導線が動作
- [ ] Docker でローカル起動が可能（ホットリロード含む）
- [ ] Lint/Format/Unit Test が通る
- [ ] README と .env サンプルが整備済み

---

## 補足: 便利コマンド（pnpm 例）
```bash
pnpm i
pnpm dev
pnpm test
pnpm build
pnpm lint
pnpm format
```

## 補足: 推奨構成（例）
```
src/
  components/
  pages/
    Login.vue
    TasksList.vue
    TaskNew.vue
  services/
    http.ts
    auth.ts
    tasks.ts
  store/
    auth.ts
    tasks.ts
  types/
    task.ts
  router/
    index.ts
  app.vue
  main.ts
```
