import { db } from "../db/db.js";
import { cache } from "../db/cache.js";

export const URLShortController = (url, expiresInDays = 1) => {
  const is_valid_url = /^(ftp|http|https):\/\/[^ "]+$/.test(url);
  if (!is_valid_url) {
    throw new Error("Invalid URL");
  }

  // Check cache first before hitting DB
  const cachedCode = cache.get(url);
  if (cachedCode) {
    console.log("Cache hit — shorten");
    return `${process.env.BASE_URL}/${cachedCode}`;
  }

  const is_url_already_shortened = db
    .prepare(
      `SELECT code, original_url FROM urls WHERE original_url = ? AND expires_at > CURRENT_TIMESTAMP`,
    )
    .get(url);

  if (is_url_already_shortened) {
    // Warm the cache for next time
    cache.set(url, is_url_already_shortened.code);
    return `${process.env.BASE_URL}/${is_url_already_shortened.code}`;
  }

  const code = Math.random().toString(36).substring(2, 8);
  const expiresAt = new Date();
  expiresAt.setDate(expiresAt.getDate() + expiresInDays);

  db.prepare(
    `INSERT INTO urls (code, original_url, expires_at) VALUES (?, ?, ?)`,
  ).run(code, url, expiresAt.toISOString());

  // Store in cache after fresh insert
  cache.set(url, code);

  return `${process.env.BASE_URL}/${code}`;
};

export const URLRedirectController = (code) => {
  // Check cache first — key is code, value is original_url
  const cachedUrl = cache.get(code);
  if (cachedUrl) {
    console.log("Cache hit — redirect");
    return cachedUrl;
  }

  const row = db
    .prepare(
      `SELECT original_url FROM urls WHERE code = ? AND expires_at > CURRENT_TIMESTAMP`,
    )
    .get(code);

  if (!row) {
    throw new Error("URL not found or has expired");
  }

  // Warm the cache for next time
  cache.set(code, row.original_url);

  return row.original_url;
};
