import express from "express";
import dotenv from "dotenv";
import { route } from "./routes/route.js";
import { startCleanupJob } from "./db/cleanupDb.js";

dotenv.config();
const app = express();
app.use(express.json());
startCleanupJob(); // Start the cleanup job when the server starts

app.use("/", route);

app.listen(3000, () => {
  console.log("Server started on port 3000");
});
