import { db } from "../db/db.js";

export const URLShortController = (url, expiresInDays = 1) => {
  const is_valid_url = /^(ftp|http|https):\/\/[^ "]+$/.test(url);

  if (!is_valid_url) {
    throw new Error("Invalid URL");
  }
  const is_url_already_shortened = db
    .prepare(
      `SELECT code,original_url FROM urls WHERE original_url = ? AND expires_at > CURRENT_TIMESTAMP`,
    )
    .get(url);

  if (is_url_already_shortened) {
    return `${process.env.BASE_URL}/${is_url_already_shortened.code}`;
  }

  const code = Math.random().toString(36).substring(2, 8);

  // Calculate expiry date
  const expiresAt = new Date();
  expiresAt.setDate(expiresAt.getDate() + expiresInDays);

  const insert = db.prepare(`
    INSERT INTO urls (code, original_url, expires_at)
    VALUES (?, ?, ?)
  `);

  insert.run(code, url, expiresAt.toISOString());
  console.log(process.env.BASE_URL);
  return `${process.env.BASE_URL}/${code}`;
};

export const URLRedirectController = (urlId) => {
  const row = db
    .prepare(
      `
    SELECT original_url FROM urls
    WHERE code = ?
    AND expires_at > CURRENT_TIMESTAMP
  `,
    )
    .get(urlId);

  if (!row) {
    throw new Error("URL not found or has expired");
  }

  return row.original_url;
};
