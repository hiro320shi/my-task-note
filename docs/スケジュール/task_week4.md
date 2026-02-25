# 4週目：AWSデプロイ（バックエンド）タスク（1日単位チェックリスト）（9/22～9/28）

## 1日目：AWS環境準備
- [ ] AWS CLI と SAM CLI のインストール確認
- [ ] AWS アカウントと CLI 認証設定
- [ ] デプロイ用 S3 バケット作成（SAM パッケージ用）
- [ ] Lambda 用 IAM ロール作成（Aurora 接続権限含む）

## 2日目：Aurora Serverless セットアップ
- [ ] Aurora Serverless クラスター作成（MySQL 互換）
- [ ] DB ネットワーク設定（VPC, サブネット, セキュリティグループ）
- [ ] DB ユーザ・パスワード設定
- [ ] Secrets Manager に接続情報保存

## 3日目：Spring Boot Lambda パッケージ作成
- [ ] Spring Boot アプリを AWS Lambda 対応にビルド
- [ ] `sam build` でパッケージ作成
- [ ] 必要な依存ライブラリを確認・含める
- [ ] ローカルで `sam local invoke` による動作確認

## 4日目：Lambda デプロイ
- [ ] `sam package` でデプロイ用アーティファクト作成
- [ ] `sam deploy` による Lambda デプロイ
- [ ] Lambda ログ確認（CloudWatch）

## 5日目：Aurora 接続確認
- [ ] Lambda から Aurora Serverless への接続テスト
- [ ] Secrets Manager からの認証情報取得確認
- [ ] 接続エラーがあれば修正

## 6日目：API Gateway 設定
- [ ] API Gateway 作成（REST または HTTP API）
- [ ] Lambda 関数を API Gateway に紐付け
- [ ] ステージ設定・デプロイ
- [ ] エンドポイント URL の確認

## 7日目：総合テストとドキュメント
- [ ] Postman / curl で API エンドポイント動作確認
- [ ] 認証・CRUD 動作テスト
- [ ] デプロイ手順・接続方法を README に記載
- [ ] 最終 Git コミット・プッシュ
