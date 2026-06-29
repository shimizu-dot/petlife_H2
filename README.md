# Pet Life Plus

Pet Life Plus は、ペットの健康記録、予約管理、診療・相談履歴、請求、通知、AI 症状チェックをまとめて扱う Spring Boot アプリケーションです。

## 構成

- `frontend/public/`: 静的サイト
- `backend/`: Spring Boot + Thymeleaf + MyBatis + H2
- `design/`: 画面・ロジック・クラス図の設計資料
- `docs/`: 補足ドキュメント

## 主な機能

- ログイン認証と権限制御
- ダッシュボード表示
- ユーザー管理、ペット管理、健康記録管理
- 診療・相談予約管理、予約枠管理
- 診療・相談履歴管理
- 請求・決済管理
- 通知・リマインド配信
- AI 症状チェック
- チャットボット相談
- LINE / Slack / Zoom 連携
- PDF 出力、画像アップロード、DB バックアップ

## ローカル起動

Docker Compose で起動します。

```bash
docker compose up --build
```

よく使うコマンド:

```bash
docker compose up -d
docker compose down
docker compose down -v
```

起動後のアクセス先:

- アプリ: `http://localhost:8080`

## Spring Boot を直接起動する場合

`backend` ディレクトリで Maven Wrapper を使って起動します。

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Windows の場合は `mvnw.cmd` を使います。

```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

H2 を使うため、別途データベースを起動する必要はありません。

## 必要な環境変数

`backend/src/main/resources/application.properties` で参照される主な環境変数は次の通りです。

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER_CLASS_NAME`
- `APP_BASE_URL`
- `OPENAI_API_KEY`, `OPENAI_MODEL`, `OPENAI_BASE_URL`
- `CHATBOT_API_KEY`, `CHATBOT_MODEL`, `CHATBOT_BASE_URL`
- `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`
- `SLACK_BOT_TOKEN`, `SLACK_SIGNING_SECRET`
- `LINE_CHANNEL_TOKEN`, `LINE_CHANNEL_SECRET`
- `ZOOM_ACCOUNT_ID`, `ZOOM_CLIENT_ID`, `ZOOM_CLIENT_SECRET`
- `UPLOAD_DIR`

未設定でも起動できる機能はありますが、外部連携は無効またはフォールバック動作になります。

## 運用メモ

- パスワード再設定メールのリンク先は `APP_BASE_URL` で決まります。
- 画像アップロードは `uploads/` 配下に保存されます。
- DB バックアップ機能は H2 の `SCRIPT` / `RUNSCRIPT` を使います。

## 参考資料

- [要件定義まとめ](backend/docs/requirements.md)
- [DB 設計](backend/docs/db-design.md)
- [テスト報告書](backend/docs/test_report.md)
- [画面・構成メモ](FOLDER_STRUCTURE.md)
