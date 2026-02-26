import express from "express";
import {
  URLRedirectController,
  URLShortController,
} from "../controllers/URLShortController.js";
import { shortenLimiter, redirectLimiter } from "../middleware/rateLimiter.js";
import { AnalyticsController } from "../controllers/analyticsController.js";

export const route = express.Router();

route.get("/", (req, res) => {
  res.send("Welcome to URL Shortener");
});

route.post("/shorten", shortenLimiter, (req, res) => {
  const { url } = req.body;
  if (!url) {
    return res.status(400).send("Please provide a URL to shorten.");
  }
  const shortenedUrl = URLShortController(url);
  res.status(200).json(shortenedUrl);
});

route.get("/:urlId", redirectLimiter, (req, res) => {
  const { urlId } = req.params;
  try {
    const originalUrl = URLRedirectController(urlId, req);
    res.redirect(originalUrl);
  } catch (error) {
    console.error(error);
    res.status(404).send("URL not found");
  }
});

// routes/route.js

route.get("/analytics/:code", (req, res) => {
  const { code } = req.params;

  console.log(code);
  const analyticsData = AnalyticsController(code);

  const { url, created_at, total_clicks, clicks_by_day, top_referrers } =
    analyticsData;

  if (!url) {
    return res.status(404).json({ error: "URL not found" });
  }
 

  res.json({
    url: url,
    created_at: created_at,
    total_clicks: total_clicks,
    clicks_by_day: clicks_by_day,
    top_referrers: top_referrers,
  }).status(200);
});
