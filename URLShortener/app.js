import express from "express";
import {route} from "./routes/route.js";

const app = express();
app.use(express.json());


app.use("/",route);

app.listen(3000,()=>{
    console.log("Server started on port 3000");
})