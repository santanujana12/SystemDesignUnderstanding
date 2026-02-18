import rateLimit from "express-rate-limit";

export const shortenLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 10, // max 10 requests per 15 minutes
  message: {
    error: "Too many requests, please try again after 15 minutes.",
  },
  standardHeaders: true, // sends rate limit info in response headers
  legacyHeaders: false,
});

export const redirectLimiter = rateLimit({
  windowMs: 1 * 60 * 1000, // 1 minute
  max: 30, // max 30 redirects per minute
  message: {
    error: "Too many requests, please try again after a minute.",
  },
  standardHeaders: true,
  legacyHeaders: false,
});
