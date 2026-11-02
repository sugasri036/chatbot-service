
import http from "node:http";
import fs from "node:fs";

const OLLAMA_URL = "http://127.0.0.1:11434";
const PORT = 3001;
const TOKEN = fs.readFileSync(".proxy-token", "utf8").trim();

if (!TOKEN) {
  console.error("Proxy token is missing");
  process.exit(1);
}

const allowedPaths = new Set(["/api/tags", "/api/chat", "/api/embed"]);

const server = http.createServer(async (req, res) => {
  if (req.headers.authorization !== `Bearer ${TOKEN}`) {
    res.writeHead(401);
    return res.end("Unauthorized");
  }

  if (!allowedPaths.has(req.url?.split("?")[0])) {
    res.writeHead(403);
    return res.end("Forbidden");
  }

  try {
    const chunks = [];
    for await (const chunk of req) chunks.push(chunk);

    const response = await fetch(`${OLLAMA_URL}${req.url}`, {
      method: req.method,
      headers: { "Content-Type": "application/json" },
      body: chunks.length ? Buffer.concat(chunks) : undefined
    });

    res.writeHead(response.status, {
      "Content-Type": response.headers.get("content-type") || "application/json"
    });

    if (!response.body) return res.end();

    for await (const chunk of response.body) res.write(chunk);
    res.end();
  } catch (error) {
    console.error(error);
    res.writeHead(502);
    res.end("Ollama unavailable");
  }
});

server.listen(PORT, "127.0.0.1", () => {
  console.log("Protected Ollama proxy running on port 3001");
});
