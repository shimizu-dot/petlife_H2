-- PetLifePlus seed data  (idempotent — H2 PostgreSQL mode / NOT EXISTS でリラン安全)
-- spring.sql.init.mode=always により起動時に自動実行される。
-- 手動実行: H2 console もしくは Spring Boot 起動時に自動実行
--
-- 実行順序（依存関係）:
--   roles → users → plans → plan_features → pets → health_records → pet_care_records → subscriptions

-- ─── Roles ───────────────────────────────────────────────────────────────────

MERGE INTO roles (id, role_code, role_name) KEY(id) VALUES (1, 'ADMIN', '管理者');
MERGE INTO roles (id, role_code, role_name) KEY(id) VALUES (2, 'SUPER', '開発者');
MERGE INTO roles (id, role_code, role_name) KEY(id) VALUES (3, 'USER',  '一般ユーザー');
MERGE INTO roles (id, role_code, role_name) KEY(id) VALUES (4, 'VET',   '獣医師');
MERGE INTO roles (id, role_code, role_name) KEY(id) VALUES (5, 'STAFF', 'スタッフ');

-- ─── Users ───────────────────────────────────────────────────────────────────
-- BCrypt ハッシュ (rounds=10) で保存。MERGE により既存ユーザーのパスワードは上書きしない。
-- 本番環境では SQL_INIT_MODE=never を設定してシードを無効化すること。

MERGE INTO users (role_id, name, email, password_hash, phone, status) KEY(email) VALUES
    (2, '開発者アカウント', 'super@petlife.local',
        '$2b$10$4./QoZjk9g1Cn6UPGqfR6exXgJGGL5I7lrbs2ftAb4dDRJx86XwwO',
        '090-1455-3927', 'ACTIVE');
MERGE INTO users (role_id, name, email, password_hash, phone, status) KEY(email) VALUES
    (1, '管理アカウント', 'admin@petlife.local',
        '$2b$10$K7fCeqiDwBij83aTS/Kgk.d.piu9oSV6cNI0IzEW2H9jYlqtmUPIG',
        '090-1111-1111', 'ACTIVE');
MERGE INTO users (role_id, name, email, password_hash, phone, status) KEY(email) VALUES
    (4, 'Dr.アカウント', 'vet1@petlife.local',
        '$2b$10$1uHCC4PGn9BmFFPZb.XGEuAmS/YijKs2XwCDxxPwfwxS.R7HngAV.',
        '090-4444-4444', 'ACTIVE');
MERGE INTO users (role_id, name, email, password_hash, phone, status) KEY(email) VALUES
    (5, 'Staff.アカウント', 'staff1@petlife.local',
        '$2b$10$skT/UtWR2yTlemeQwvDKWeWBQ72edma27MlZop/fEGnITM7f970.e',
        '090-5555-5555', 'ACTIVE');
MERGE INTO users (role_id, name, email, password_hash, phone, status) KEY(email) VALUES
    (3, 'ライト会員', 'owner1@petlife.local',
        '$2b$10$QzvJ9Z/cZr7wxqa4QFo69.rXFXQmgw6ys.nHZP1.TjJ7U864xIpQy',
        '090-6666-0001', 'ACTIVE');
MERGE INTO users (role_id, name, email, password_hash, phone, status) KEY(email) VALUES
    (3, 'スタンダード会員', 'owner2@petlife.local',
        '$2b$10$QzvJ9Z/cZr7wxqa4QFo69.rXFXQmgw6ys.nHZP1.TjJ7U864xIpQy',
        '090-6666-0002', 'ACTIVE');
MERGE INTO users (role_id, name, email, password_hash, phone, status) KEY(email) VALUES
    (3, 'プレミアム会員', 'owner3@petlife.local',
        '$2b$10$QzvJ9Z/cZr7wxqa4QFo69.rXFXQmgw6ys.nHZP1.TjJ7U864xIpQy',
        '090-6666-0003', 'ACTIVE');

-- ─── Plans ───────────────────────────────────────────────────────────────────

MERGE INTO plans (id, name, monthly_fee, features_json, is_active, deleted_at, created_at, updated_at) KEY(id) VALUES (
    1, 'LIGHT', 980.00,
    '{"healthRecord":true,"basicNotification":true,"aiSymptomCheck":false,"slackBot":false,"lineBot":false,"zoomConsult":false}',
    true, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
MERGE INTO plans (id, name, monthly_fee, features_json, is_active, deleted_at, created_at, updated_at) KEY(id) VALUES (
    2, 'STANDARD', 1980.00,
    '{"healthRecord":true,"basicNotification":true,"aiSymptomCheck":true,"slackBot":true,"lineBot":true,"zoomConsult":false}',
    true, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
MERGE INTO plans (id, name, monthly_fee, features_json, is_active, deleted_at, created_at, updated_at) KEY(id) VALUES (
    3, 'PREMIUM', 2980.00,
    '{"healthRecord":true,"basicNotification":true,"aiSymptomCheck":true,"slackBot":true,"lineBot":true,"zoomConsult":true}',
    true, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- ─── Plan features ────────────────────────────────────────────────────────────
-- LIGHT (1): 基本機能のみ（予約・AI_SYMPTOM / Slack / LINE / Zoom は含まない）
-- STANDARD (2): 予約・AI症状チェック・Slack・LINE
-- PREMIUM (3): STANDARD 全機能 + Zoom オンライン診療

MERGE INTO plan_features (plan_id, feature_code) KEY(plan_id, feature_code) VALUES (2, 'APPOINTMENT');
MERGE INTO plan_features (plan_id, feature_code) KEY(plan_id, feature_code) VALUES (2, 'AI_SYMPTOM');
MERGE INTO plan_features (plan_id, feature_code) KEY(plan_id, feature_code) VALUES (2, 'SLACK_BOT');
MERGE INTO plan_features (plan_id, feature_code) KEY(plan_id, feature_code) VALUES (2, 'LINE_BOT');
MERGE INTO plan_features (plan_id, feature_code) KEY(plan_id, feature_code) VALUES (3, 'APPOINTMENT');
MERGE INTO plan_features (plan_id, feature_code) KEY(plan_id, feature_code) VALUES (3, 'AI_SYMPTOM');
MERGE INTO plan_features (plan_id, feature_code) KEY(plan_id, feature_code) VALUES (3, 'SLACK_BOT');
MERGE INTO plan_features (plan_id, feature_code) KEY(plan_id, feature_code) VALUES (3, 'LINE_BOT');
MERGE INTO plan_features (plan_id, feature_code) KEY(plan_id, feature_code) VALUES (3, 'ZOOM_CONSULT');

-- ─── Pets ────────────────────────────────────────────────────────────────────
-- owner1@petlife.local (Light プラン)

INSERT INTO pets (id, owner_user_id, name, species, breed, sex, birth_date, weight_baseline_kg, image_path)
SELECT 1, u.id, 'ポチ', 'DOG', '雑種', 'MALE', '2021-03-01', 8.50, '/assets/img/dog-01.png'
FROM users u WHERE u.email = 'owner1@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM pets WHERE id = 1);

INSERT INTO pets (id, owner_user_id, name, species, breed, sex, birth_date, weight_baseline_kg, image_path)
SELECT 2, u.id, 'タロウ', 'DOG', '柴犬', 'FEMALE', '2022-07-12', 3.80, '/assets/img/dog-02.png'
FROM users u WHERE u.email = 'owner1@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM pets WHERE id = 2);

-- owner2@petlife.local (Standard プラン)

INSERT INTO pets (id, owner_user_id, name, species, breed, sex, birth_date, weight_baseline_kg, image_path)
SELECT 3, u.id, 'レオン', 'DOG', 'ノーフォークテリア', 'MALE', '2020-11-23', 5.20, '/assets/img/dog-03.png'
FROM users u WHERE u.email = 'owner2@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM pets WHERE id = 3);

-- プラン別テストアカウント用ペット

INSERT INTO pets (id, owner_user_id, name, species, breed, sex, birth_date, weight_baseline_kg, image_path)
SELECT 101, u.id, 'ピーコ', 'DOG', 'チワワ', 'FEMALE', '2022-01-01', 7.40, '/assets/img/dog-04.png'
FROM users u WHERE u.email = 'owner1@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM pets p WHERE p.id = 101 OR (p.owner_user_id = u.id AND p.name = 'ピーコ'));

INSERT INTO pets (id, owner_user_id, name, species, breed, sex, birth_date, weight_baseline_kg, image_path)
SELECT 102, u.id, 'カレン', 'DOG', 'ポメプー', 'FEMALE', '2021-06-10', 4.10, '/assets/img/dog-05.png'
FROM users u WHERE u.email = 'owner2@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM pets p WHERE p.id = 102 OR (p.owner_user_id = u.id AND p.name = 'カレン'));

INSERT INTO pets (id, owner_user_id, name, species, breed, sex, birth_date, weight_baseline_kg, image_path)
SELECT 103, u.id, 'ボス', 'DOG', 'パグ', 'MALE', '2020-04-20', 5.60, '/assets/img/dog-01.png'
FROM users u WHERE u.email = 'owner3@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM pets p WHERE p.id = 103 OR (p.owner_user_id = u.id AND p.name = 'ボス'));

-- 既存レコードへのペット画像パス反映（idempotent）
UPDATE pets SET image_path = '/assets/img/dog-01.png' WHERE id = 1   AND image_path IS NULL;
UPDATE pets SET image_path = '/assets/img/dog-02.png' WHERE id = 2   AND image_path IS NULL;
UPDATE pets SET image_path = '/assets/img/dog-03.png' WHERE id = 3   AND image_path IS NULL;
UPDATE pets SET image_path = '/assets/img/dog-04.png' WHERE id = 101 AND image_path IS NULL;
UPDATE pets SET image_path = '/assets/img/dog-05.png' WHERE id = 102 AND image_path IS NULL;
UPDATE pets SET image_path = '/assets/img/dog-01.png' WHERE id = 103 AND image_path IS NULL;

-- ─── Health records ──────────────────────────────────────────────────────────

INSERT INTO health_records (id, pet_id, recorded_by_user_id, record_date, weight_kg, meal_memo, exercise_minutes, meal_score, exercise_score, sleep_score, mood_score, overall_score, note)
SELECT 1, 1, u.id, '2026-05-10', 8.60, '食欲良好', 30, 5, 4, 5, 5, 5, '特記事項なし'
FROM users u WHERE u.email = 'owner1@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM health_records WHERE id = 1);

INSERT INTO health_records (id, pet_id, recorded_by_user_id, record_date, weight_kg, meal_memo, exercise_minutes, meal_score, exercise_score, sleep_score, mood_score, overall_score, note)
SELECT 2, 2, u.id, '2026-05-10', 3.75, '少し食欲低下', 15, 3, 2, 4, 3, 3, '様子見'
FROM users u WHERE u.email = 'owner1@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM health_records WHERE id = 2);

INSERT INTO health_records (id, pet_id, recorded_by_user_id, record_date, weight_kg, meal_memo, exercise_minutes, meal_score, exercise_score, sleep_score, mood_score, overall_score, note)
SELECT 3, 3, u.id, '2026-05-11', 5.25, '通常', 25, 4, 4, 4, 4, 4, '元気'
FROM users u WHERE u.email = 'owner2@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM health_records WHERE id = 3);

-- 既存レコードへのスコア反映（idempotent）
UPDATE health_records SET meal_score=5, exercise_score=4, sleep_score=5, mood_score=5, overall_score=5 WHERE id=1 AND overall_score IS NULL;
UPDATE health_records SET meal_score=3, exercise_score=2, sleep_score=4, mood_score=3, overall_score=3 WHERE id=2 AND overall_score IS NULL;
UPDATE health_records SET meal_score=4, exercise_score=4, sleep_score=4, mood_score=4, overall_score=4 WHERE id=3 AND overall_score IS NULL;

-- ─── Pet care records ────────────────────────────────────────────────────────

INSERT INTO pet_care_records (id, pet_id, recorded_by_user_id, care_type, administered_on, next_due_on, memo)
SELECT 1, 1, u.id, 'RABIES', '2025-08-01', '2026-08-01', '狂犬病予防接種'
FROM users u WHERE u.email = 'owner1@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM pet_care_records WHERE id = 1);

INSERT INTO pet_care_records (id, pet_id, recorded_by_user_id, care_type, administered_on, next_due_on, memo)
SELECT 2, 1, u.id, 'HEARTWORM', '2025-07-15', '2026-07-15', 'フィラリア予防薬'
FROM users u WHERE u.email = 'owner1@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM pet_care_records WHERE id = 2);

INSERT INTO pet_care_records (id, pet_id, recorded_by_user_id, care_type, administered_on, next_due_on, memo)
SELECT 3, 1, u.id, 'COMBO_VACCINE', '2025-09-01', '2026-09-01', '混合ワクチン接種'
FROM users u WHERE u.email = 'owner1@petlife.local'
  AND NOT EXISTS (SELECT 1 FROM pet_care_records WHERE id = 3);

-- ─── Subscriptions ───────────────────────────────────────────────────────────
-- サブスクリプションはオーナー単位（ACTIVE は 1 件）

-- owner1 → LIGHT
INSERT INTO subscriptions (user_id, plan_id, start_date, status, auto_renew)
SELECT u.id, 1, CURRENT_DATE - INTERVAL '30 days', 'ACTIVE', true
FROM users u
WHERE u.email = 'owner1@petlife.local'
  AND NOT EXISTS (
      SELECT 1 FROM subscriptions s WHERE s.user_id = u.id AND s.status = 'ACTIVE' AND s.deleted_at IS NULL
  );

-- owner2 → STANDARD
INSERT INTO subscriptions (user_id, plan_id, start_date, status, auto_renew)
SELECT u.id, 2, CURRENT_DATE - INTERVAL '30 days', 'ACTIVE', true
FROM users u
WHERE u.email = 'owner2@petlife.local'
  AND NOT EXISTS (
      SELECT 1 FROM subscriptions s WHERE s.user_id = u.id AND s.status = 'ACTIVE' AND s.deleted_at IS NULL
  );

-- owner3 → PREMIUM
INSERT INTO subscriptions (user_id, plan_id, start_date, status, auto_renew)
SELECT u.id, 3, CURRENT_DATE - INTERVAL '30 days', 'ACTIVE', true
FROM users u
WHERE u.email = 'owner3@petlife.local'
  AND NOT EXISTS (
      SELECT 1 FROM subscriptions s WHERE s.user_id = u.id AND s.status = 'ACTIVE' AND s.deleted_at IS NULL
  );

-- ─── Sequence fixes ──────────────────────────────────────────────────────────
-- 明示 ID を使った INSERT 後は IDENTITY の開始値を次へ進める（次の AUTO INSERT が重複しないよう）
ALTER TABLE roles ALTER COLUMN id RESTART WITH 6;
ALTER TABLE users ALTER COLUMN id RESTART WITH 8;
ALTER TABLE plans ALTER COLUMN id RESTART WITH 4;
ALTER TABLE pets ALTER COLUMN id RESTART WITH 104;
ALTER TABLE health_records ALTER COLUMN id RESTART WITH 4;
ALTER TABLE pet_care_records ALTER COLUMN id RESTART WITH 4;

MERGE INTO appointment_business_hours (id, business_start, business_end, slot_minutes, updated_by_user_id, created_at, updated_at) KEY(id) VALUES (
    1, '09:30', '17:00', 30, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
