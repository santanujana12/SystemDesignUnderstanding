const express = require("express");
const http = require("node:http");
const app = express();
const os = require("node:os");


// Get port from command line arguments
const port = process.argv[2] || 3000;

app.get("/", (req, res) => {
  console.log(os.cpus().length);
  res.send(`Load Balancer received request at ${new Date().toISOString()}`);
});

// app.listen(port, () => {
//   console.log(`Express Backend started on port ${port}`);
// });

// Configuration
const servers = [
  { host: "localhost", port: 3001 },
  { host: "localhost", port: 3002 },
  { host: "localhost", port: 3003 },
];

let currentServerIndex = 0;

// Catch-all route handler for the Load Balancer
app.use((clientReq, clientRes) => {
  // 1. Round Robin Selection
  
  const target = servers[currentServerIndex];
  currentServerIndex = (currentServerIndex + 1) % servers.length;

  console.log(
    `[LB] Proxying ${clientReq.method} ${clientReq.url} --> ${target.host}:${target.port}`,
  );

  // 2. Prepare options
  const options = {
    hostname: target.host,
    port: target.port,
    path: clientReq.url,
    method: clientReq.method,
    headers: clientReq.headers,
  };

  // 3. Make the request to the backend
  const proxyReq = http.request(options, (proxyRes) => {
    // Forward status code and headers
    clientRes.writeHead(proxyRes.statusCode, proxyRes.headers);

    // Pipe the response from backend -> client
    proxyRes.pipe(clientRes, { end: true });
  });

  // Error Handling (Backend down)
  proxyReq.on("error", (err) => {
    console.error(`Error connecting to backend ${target.port}`);
    clientRes.status(502).send("Bad Gateway: Backend server is down.");
  });

  // 4. Pipe the request body from client -> backend
  // (This forwards POST data, file uploads, etc.)
  clientReq.pipe(proxyReq, { end: true });
});

const PORT = 8080;
app.listen(PORT, () => {
  console.log(`Express Load Balancer running on port ${PORT}`);
});