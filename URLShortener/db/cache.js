import NodeCache from "node-cache";

// TTL = time to live in seconds, checks for expired keys every 60 seconds
export const cache = new NodeCache({ stdTTL: 3600, checkperiod: 60 });
