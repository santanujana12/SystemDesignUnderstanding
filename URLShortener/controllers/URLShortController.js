const urlDatabase = {};

export const URLShortController = (url) => {
  // Logic to shorten the URL
  const generateIdForUrl = Math.random().toString(36).substring(2, 8);
  urlDatabase[generateIdForUrl] = url;
  return `${process.env.BASE_URL}/${generateIdForUrl}`;
};

export const URLRedirectController = (urlId) => {
  // Logic to redirect to the original URL
  const originalUrl = urlDatabase[urlId];
  if (!originalUrl) {
    throw new Error("URL not found");
  }
  return originalUrl;
};
