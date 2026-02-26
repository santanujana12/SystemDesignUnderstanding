import { db } from "../db/db.js";

export function trackAnalytics(code, req) {
  const ip = req.ip || req.socket.remoteAddress;
  const userAgent = req.get("user-agent") || "";
  const referrer = req.get("referer") || "direct";

  // Insert analytics asynchronously so it doesn't block the redirect
  setImmediate(() => {
    try {
      db.prepare(
        `
        INSERT INTO analytics (code, ip_address, user_agent, referrer)
        VALUES (?, ?, ?, ?)
      `,
      ).run(code, ip, userAgent, referrer);
    } catch (error) {
      console.error("Analytics tracking failed:", error);
    }
  });
}

export const AnalyticsController = (code) => {
  // Check if URL exists
  const url = db
    .prepare(
      `
    SELECT original_url, created_at FROM urls WHERE code = ?
  `,
    )
    .get(code);

  if (!url) {
    throw new Error("URL not found");
  }

  // Get total clicks (handle null result)
  const totalClicksResult = db
    .prepare(
      `
    SELECT COUNT(*) as count FROM analytics WHERE code = ?
  `,
    )
    .get(code);

  const totalClicks = totalClicksResult ? totalClicksResult.count : 0;

  // Get clicks by day
  const clicksByDay = db
    .prepare(
      `
    SELECT 
      DATE(timestamp) as date,
      COUNT(*) as clicks
    FROM analytics
    WHERE code = ?
    AND timestamp >= datetime('now', '-7 days')
    GROUP BY DATE(timestamp)
    ORDER BY date DESC
  `,
    )
    .all(code);

  // Get top referrers
  const topReferrers = db
    .prepare(
      `
    SELECT 
      referrer,
      COUNT(*) as clicks
    FROM analytics
    WHERE code = ?
    GROUP BY referrer
    ORDER BY clicks DESC
    LIMIT 5
  `,
    )
    .all(code);


  return {
    url: url.original_url,
    created_at: url.created_at,
    total_clicks: totalClicks,
    clicks_by_day: clicksByDay,
    top_referrers: topReferrers,
  };
};
