import express from "express";
import {
  URLRedirectController,
  URLShortController,
} from "../controllers/URLShortController.js";
import { shortenLimiter, redirectLimiter } from "../middleware/rateLimiter.js";

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
    const originalUrl = URLRedirectController(urlId);
    res.redirect(originalUrl);
  } catch (error) {
    console.error(error);
    res.status(404).send("URL not found");
  }
});
