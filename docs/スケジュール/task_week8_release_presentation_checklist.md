# 8週目：リリース & 発表準備（チェックリスト）（10/20～10/26）
対象: My Task Note（Vue + Lambda + Aurora + AWS 無料枠運用）

---

## 目標（Definition of Done）
- [ ] 本番環境へデプロイ（フロント: S3+CloudFront / バック: Lambda+API Gateway / DB: Aurora）
- [ ] 動作確認（ユーザ登録→ログイン→タスクCRUDが本番で動作）
- [ ] 発表用資料完成（構成図、手順、学びまとめ）
- [ ] 発表リハーサル実施

---

## Day 1: 本番環境デプロイ準備
- [ ] 本番用 S3 バケット作成（`mytasknote-prod-web`）
- [ ] 本番用 CloudFront ディストリビューション作成（HTTPS, OAC）
- [ ] バックエンド（SAM）本番スタック作成（`MyTaskNote-Prod`）
- [ ] Secrets Manager に本番用 DB 接続情報を登録
- [ ] Aurora Serverless（prod DB クラスタ）作成確認
- [ ] GitHub Actions の環境（production）設定 → Secrets/Vars に prod 用を登録

**成果物**
- [ ] AWS コンソールに prod リソースが揃っていること

---

## Day 2: 本番デプロイ実行（フロント/バック）
- [ ] フロントエンド本番ビルド & S3 へデプロイ（GitHub Actions → OIDC AssumeRole）
- [ ] CloudFront 無効化（キャッシュクリア）
- [ ] バックエンド SAM デプロイ（prod スタック）
- [ ] Lambda バージョン/エイリアス設定（blue/green 運用検討）
- [ ] デプロイログを保存（GitHub Actions の履歴、AWS CLI 出力をスクショ）

**成果物**
- [ ] https://{cloudfront_domain}/login にアクセスしてログイン画面表示を確認

---

## Day 3: 動作確認（本番環境）
- [ ] サインアップ（新規ユーザ登録）→ DB 登録確認
- [ ] ログイン → JWT 受信確認（開発者ツール Network）
- [ ] タスク登録 → Aurora に反映
- [ ] タスク一覧表示 → API 連携 & 表示確認
- [ ] タスク更新/削除 → 正しく反映されること
- [ ] エラーケース（不正ログイン/必須項目不足/ネットワーク遮断）挙動確認
- [ ] CloudWatch Logs にエラーログが記録されているか確認

**成果物**
- [ ] 動作確認チェックリストに「OK」と記録
- [ ] 不具合があれば Issue 化

---

## Day 4: 発表資料作成（構成図）
- [X] 全体構成図（フロント、API Gateway、Lambda、Aurora、Secrets Manager）
- [X] CI/CD パイプライン図（GitHub Actions → AWS デプロイ）
- [X] ER 図（ユーザ、タスクテーブル）
- [X] シーケンス図（ユーザ登録フロー、ログインフロー、タスクCRUD）
- [X] 図を Markdown + Mermaid or Draw.io で作成

**成果物**
- [X] `/docs/presentation/architecture.md` に図を保存

---

## Day 5: 発表資料作成（手順・まとめ）
- [X] 開発手順まとめ（週ごとの進捗）
- [X] 使用技術スタック一覧（Vue, Vite, Spring Boot, Lambda, Aurora, GitHub Actions, Docker, etc.）
- [X] 学び/気づきまとめ
  - AWS 無料枠の運用ポイント
  - CI/CD の自動化体験
  - サーバーレス開発のメリット/課題
- [X] 今後の課題・改善案（認証強化、監視、UI/UX 改善など）

**成果物**
- [X] 発表スライド草案（Google Slides or PowerPoint）

---

## Day 6: 発表スライド完成 & リハーサル
- [X] 発表スライドを完成（タイトル、背景、目的、構成、成果、学び、今後）
- [X] スライドに構成図・スクショを挿入
- [X] 発表時間を計測（5〜10分想定）
- [X] リハーサル実施（声出し・録画確認）
- [X] フィードバックをもとに修正

**成果物**
- [X] 発表スライド最終版（`/docs/presentation/final.pptx` or `.pdf`）

---

## Day 7: 最終確認 & 本番
- [X] 本番環境の最終動作確認（ユーザ登録〜タスクCRUD）
- [X] 発表スライド最終レビュー
- [X] 発表リハーサル（本番想定環境で）
- [X] 発表実施！

**成果物**
- [X] 発表終了後、成果物と資料を GitHub リポジトリに整理保存

---

## 補足: 発表スライド構成案
1. タイトル（アプリ名、発表者）
2. 背景と目的（なぜ作ったか）
3. 全体アーキテクチャ図
4. 開発ステップ（週ごとの進捗）
5. デモ（スクショ or 動画）
6. 学びと課題
7. まとめと今後の展望

