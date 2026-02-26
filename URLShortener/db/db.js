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

db.exec(`
  CREATE TABLE IF NOT EXISTS analytics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    ip_address TEXT,
    user_agent TEXT,
    referrer TEXT,
    FOREIGN KEY (code) REFERENCES urls(code)
  )
`);


// Indexing for faster lookups
db.exec(`
  CREATE INDEX IF NOT EXISTS idx_analytics_code ON analytics(code);
  CREATE INDEX IF NOT EXISTS idx_analytics_timestamp ON analytics(timestamp);
`);
