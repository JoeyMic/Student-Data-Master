-- SQLite create table (no separate database server required)
CREATE TABLE IF NOT EXISTS students (
  student_id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  major TEXT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
