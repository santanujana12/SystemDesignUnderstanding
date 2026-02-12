import Database from "better-sqlite3";

export const db = new Database("urlShortener.db");

// Create table if it doesn't exist
db.exec(`
  CREATE TABLE IF NOT EXISTS urls (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  code TEXT UNIQUE NOT NULL,
  original_url TEXT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  expires_at DATETIME NOT NULL
)
`);
