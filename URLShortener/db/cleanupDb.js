import cron from "node-cron";
import {db} from "./db.js";

/*

## Understanding the cron expression

`"0 0 * * *"` looks cryptic but it's just 5 fields:
```
┌─ minute      (0)
│  ┌─ hour     (0)
│  │  ┌─ day   (*)  → every day
│  │  │  ┌─ month (*) → every month
│  │  │  │  ┌─ weekday (*) → every weekday
0  0  *  *  *

*/
const deleteExpiredUrls = () => {
  const result = db
    .prepare(
      `
    DELETE FROM urls
    WHERE expires_at <= CURRENT_TIMESTAMP
  `,
    )
    .run();

  console.log(`Cleanup ran — ${result.changes} expired URLs deleted`);
};

// Runs every day at midnight
export const startCleanupJob = () => {
  cron.schedule("0 0 * * *", deleteExpiredUrls);
  console.log("Cleanup job scheduled");
};

// Ensures expired URLS are deleted to free up space and maintain performance.
