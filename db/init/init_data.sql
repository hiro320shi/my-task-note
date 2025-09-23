-- ユーザ
INSERT INTO users (name, email, password_hash) VALUES
('Taro', 'taro@example.com', 'hashed_pw1'),
('Hanako', 'hanako@example.com', 'hashed_pw2');

-- タスク
INSERT INTO tasks (user_id, title, description, status) VALUES
(1, '買い物に行く', 'スーパーで牛乳を買う', 'TODO'),
(1, '資料作成', '発表用スライドを作る', 'IN_PROGRESS'),
(2, 'ランニング', '朝5km走る', 'DONE');
