# 7週目：CI/CD & 結合テスト（チェックリスト）（10/13～10/19）
対象: My Task Note（フロント=S3+CloudFront、バックエンド=Lambda+API Gateway / SAM）  
ツール: GitHub Actions, OIDC（GitHub→AWS）, Playwright（E2E）, Vitest（ユニット）

---

## 目標（Definition of Done）
- [ ] GitHub Actions で main への push / release で **自動デプロイ**（フロント/バック）
- [ ] PR 時に **lint / unit / e2e（ステージ環境）** が自動実行
- [ ] デプロイ後に **Smoke テスト** と **CloudFront キャッシュ無効化** が自動実行
- [ ] 失敗時に **ロールバック手順**（手動 or 自動）が明文化されている
- [ ] 実行結果・アーティファクト（テストレポート/スクリーンショット）が保存・参照可能

---

## 前提
- AWS 側のステージング / 本番リソースは事前作成 or IaC で作成（S3, CloudFront, API Gateway, Lambda, RDS/Aurora など）
- バックエンドは SAM テンプレート（`template.yaml`）でデプロイ可能
- GitHub→AWS の認証は OIDC（推奨） or `AWS_ACCESS_KEY_ID/SECRET`（簡易）
- リポジトリ構成（例）
  ```text
  / (monorepo想定)
    backend/  # SAM
    frontend/ # Vite + Vue
    .github/workflows/
  ```

---

## Day 1: OIDC & AWS 権限準備（セキュア土台）
- [ ] GitHub OIDC 用の **IAM ロール** 作成（`GitHubActionsDeployRole`）
  - [ ] 信頼ポリシー: `token.actions.githubusercontent.com` を信頼、`aud: sts.amazonaws.com`、`sub` に対象リポジトリ/ブランチ制限
  - [ ] アタッチ権限（最小権限）
    - フロント: `s3:PutObject`, `s3:ListBucket`, `s3:DeleteObject`, `cloudfront:CreateInvalidation`
    - バック: `cloudformation:*`(制限推奨), `lambda:*`(対象関数のみ), `iam:PassRole`(必要最小限), `s3:*`(artifactsバケットのみ)
- [ ] GitHub Secrets / Variables を登録
  - [ ] `AWS_REGION`（例: `ap-northeast-1`）
  - [ ] `AWS_ROLE_ARN`（作成した OIDC ロール ARN）
  - [ ] `CF_DISTRIBUTION_ID`（フロント）
  - [ ] `S3_BUCKET_FRONTEND`（フロント）
  - [ ] `SAM_STACK_NAME`（バックエンド）
  - [ ] `SAM_ARTIFACTS_BUCKET`（SAM パッケージ保管用）
  - [ ] `API_BASE_URL_STG`, `API_BASE_URL_PROD`（E2E/本番用）
  - [ ] （必要なら）`PLAYWRIGHT_TEST_USER`, `PLAYWRIGHT_TEST_PASS`
- [ ] 最小権限の JSON を `/docs/iam/` に保存（監査用）

**成果物**
- [ ] IAM ロール & ポリシー定義（JSON）
- [ ] GitHub Secrets/Vars 登録一覧（ドキュメント）

---

## Day 2: CI（PR検証）ワークフロー作成
- [ ] `.github/workflows/ci.yml` 新規作成（PR/手動トリガ）
  - [ ] Node セットアップ（`actions/setup-node@v4`、Node 20）
  - [ ] フロント: `pnpm i`, `pnpm lint`, `pnpm test`
  - [ ] バック: 単体テスト／Lint（必要に応じて）
  - [ ] E2E（ステージ環境）: `playwright install --with-deps` → `pnpm e2e:stg`
  - [ ] 失敗時スクショ/動画をアーティファクト保存（`actions/upload-artifact@v4`）
  - [ ] キャッシュ（`actions/cache@v4`）で node_modules or pnpm-store を最適化
- [ ] Playwright 設定
  - [ ] `playwright.config.ts` に `BASE_URL`（env で切替）
  - [ ] `tests/e2e/` にハッピーパス: 「サインアップ→ログイン→タスク登録→一覧反映」

**チェック**
- [ ] PR を作って CI が通ること
- [ ] 失敗時、アーティファクトにログ/スクショが残ること

---

## Day 3: CD（フロント）— S3 + CloudFront
- [ ] `.github/workflows/deploy_frontend.yml` 作成（`main` push / release トリガ）
  - [ ] `aws-actions/configure-aws-credentials@v4` で OIDC AssumeRole
  - [ ] `pnpm build` → `dist/` 生成
  - [ ] **S3 同期**: `aws s3 sync dist s3://$S3_BUCKET_FRONTEND --delete`
  - [ ] **キャッシュ見直し**: `index.html` は no-cache、アセットはハッシュ付
  - [ ] **CloudFront 無効化**: `aws cloudfront create-invalidation --paths "/*"`
  - [ ] **Smoke テスト**（任意: `curl https://domain` で 200 確認）
- [ ] `.env.production` / `VITE_*` の取り扱い定義（Secrets → `env` 注入手順）

**チェック**
- [ ] main へ push でデプロイが走り、最新 UI が配信される
- [ ] 失敗時にログが明確（S3/CF 権限やパスの誤りが即特定可能）

---

## Day 4: CD（バックエンド）— SAM デプロイ
- [ ] `.github/workflows/deploy_backend.yml` 作成（`main` push / release トリガ）
  - [ ] OIDC で AssumeRole
  - [ ] SAM パッケージ&デプロイ
    ```bash
    sam build --use-container
    sam package --s3-bucket "$SAM_ARTIFACTS_BUCKET" --output-template-file packaged.yaml
    sam deploy       --template-file packaged.yaml       --stack-name "$SAM_STACK_NAME"       --capabilities CAPABILITY_NAMED_IAM       --no-fail-on-empty-changeset       --parameter-overrides StageName=prod
    ```
  - [ ] **Post-deploy Smoke**（ヘルスチェック用 Lambda / `/health` など）
  - [ ] 失敗時の通知（GitHub Status / Slack 連携は任意）
- [ ] **段階的適用**（任意）
  - [ ] 先に `stg` 環境へ自動デプロイ → E2E 合格後に `prod` へ昇格（環境別ジョブ）

**チェック**
- [ ] 同一ブランチの変更が Backend に反映される
- [ ] API 変更でダウンタイムなく差し替え（Alias/Version 運用やロールアウト戦略の検討）

---

## Day 5: 結合・E2E 強化 & ロールバック導線
- [ ] E2E シナリオ拡充（失敗系含む）
  - [ ] サインアップ：既存ユーザ（409）
  - [ ] ログイン：401 表示
  - [ ] タスク登録：必須エラー表示、API 500 モック
  - [ ] 一覧：空データ、ページング、フィルタ
- [ ] 本番デプロイ前後の **自動 E2E（ステージ）→ 手動承認 → 本番** の Gate 化
  - [ ] `environment` と `protection rules`（Required reviewers）を設定
- [ ] ロールバック手順を `/docs/release/rollback.md` に明文化
  - [ ] フロント：S3 の直前リリースを保持（`release-YYYYMMDD-HHmm` ディレクトリ）→ `sync` 差し戻し
  - [ ] バック：CloudFormation の前回スタック（または Lambda の前バージョン alias）に切替
  - [ ] 連絡手順・判断基準（SLO/エラーレート、問い合わせ数）を明確化

**チェック**
- [ ] 失敗系 E2E が CI で赤くなることを確認（意図通り）
- [ ] ロールバック手順で実際に差し戻し可能（ステージで演習）

---

## 付録：サンプル YAML（抜粋）

### `.github/workflows/deploy_frontend.yml`
```yaml
name: Deploy Frontend
on:
  push:
    branches: [ "main" ]
jobs:
  deploy:
    runs-on: ubuntu-latest
    permissions:
      id-token: write
      contents: read
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
      - run: corepack enable
      - run: pnpm i --frozen-lockfile
        working-directory: frontend
      - run: pnpm build
        working-directory: frontend
      - uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: ${{ secrets.AWS_ROLE_ARN }}
          aws-region: ${{ vars.AWS_REGION }}
      - name: Upload to S3
        run: |
          aws s3 sync frontend/dist s3://${{ vars.S3_BUCKET_FRONTEND }} --delete
      - name: Invalidate CloudFront
        run: |
          aws cloudfront create-invalidation             --distribution-id ${{ vars.CF_DISTRIBUTION_ID }}             --paths "/*"
      - name: Smoke test
        run: curl -sSf https://your.front.example.com > /dev/null
```

### `.github/workflows/deploy_backend.yml`
```yaml
name: Deploy Backend
on:
  push:
    branches: [ "main" ]
jobs:
  deploy:
    runs-on: ubuntu-latest
    permissions:
      id-token: write
      contents: read
    steps:
      - uses: actions/checkout@v4
      - uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: ${{ secrets.AWS_ROLE_ARN }}
          aws-region: ${{ vars.AWS_REGION }}
      - uses: actions/setup-python@v5
        with:
          python-version: '3.12'
      - name: Install SAM CLI
        run: pip install aws-sam-cli
      - name: Build & Package
        working-directory: backend
        run: |
          sam build --use-container
          sam package --s3-bucket ${{ vars.SAM_ARTIFACTS_BUCKET }} --output-template-file packaged.yaml
      - name: Deploy
        working-directory: backend
        run: |
          sam deploy             --template-file packaged.yaml             --stack-name ${{ vars.SAM_STACK_NAME }}             --capabilities CAPABILITY_NAMED_IAM             --no-fail-on-empty-changeset
      - name: Smoke test
        run: curl -sSf https://your.api.example.com/health > /dev/null
```

### `.github/workflows/ci.yml`（PR）
```yaml
name: CI
on:
  pull_request:
    branches: [ "main", "develop" ]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
      - run: corepack enable
      - run: pnpm i --frozen-lockfile
        working-directory: frontend
      - run: pnpm lint && pnpm test
        working-directory: frontend
      - name: Install Playwright
        run: npx playwright install --with-deps
      - name: E2E (stg)
        env:
          BASE_URL: ${{ vars.FRONTEND_STG_URL }}
          API_BASE_URL: ${{ vars.API_BASE_URL_STG }}
          E2E_USER: ${{ secrets.PLAYWRIGHT_TEST_USER }}
          E2E_PASS: ${{ secrets.PLAYWRIGHT_TEST_PASS }}
        run: pnpm e2e:stg
        working-directory: frontend
      - name: Upload artifacts
        if: failure()
        uses: actions/upload-artifact@v4
        with:
          name: e2e-artifacts
          path: frontend/test-results/**
```

---

## 付録：E2E シナリオ（例）
- [ ] サインアップ（新規メール/ID で登録 → 成功メッセージ）
- [ ] ログイン（正しい資格情報で /tasks に遷移）
- [ ] タスク登録（タイトル必須 → 正常登録 → 一覧に反映）
- [ ] ログアウト（トークン破棄 → /login へ）
- [ ] 失敗系（401/409/500 の UI 表示検証）

---

## 付録：トラブルシュート
- [ ] CloudFront で新アセットが表示されない → 無効化パス/キャッシュ制御を確認
- [ ] SAM の権限不足 → ロールの `iam:PassRole` 対象を限定しつつ必要権限を追加
- [ ] OIDC AssumeRole 失敗 → `sub` 条件のミスマッチ（`repo:owner/name:ref:refs/heads/main` 等）
- [ ] Playwright 失敗 → スクショ/動画をアーティファクトから確認、`BASE_URL` の環境差異を見直し

