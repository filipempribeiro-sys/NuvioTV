import http from "node:http";
import crypto from "node:crypto";

const PORT = Number(process.env.PORT || 8080);
const BASE_URL = process.env.MEDIAHUB_PUBLIC_BASE_URL || `http://localhost:${PORT}`;
const PAIRING_TTL = Math.max(60, Number(process.env.MEDIAHUB_PAIRING_TTL_SECONDS || 300));
const PRODUCT = "MEDIA•HUB";
const challenges = new Map();

const json = (res, status, body) => {
  res.writeHead(status, { "content-type": "application/json; charset=utf-8", "cache-control": "no-store" });
  res.end(JSON.stringify(body));
};

const cleanExpired = () => {
  const now = Date.now();
  for (const [id, value] of challenges) if (value.expiresAt <= now || value.consumed) challenges.delete(id);
};

const randomCode = () => crypto.randomBytes(4).toString("hex").toUpperCase();
const randomId = () => crypto.randomUUID();

const server = http.createServer(async (req, res) => {
  cleanExpired();
  const url = new URL(req.url, BASE_URL);

  if (req.method === "GET" && url.pathname === "/health") {
    return json(res, 200, { status: "ok", product: PRODUCT });
  }

  if (req.method === "GET" && url.pathname === "/.well-known/mediahub") {
    return json(res, 200, {
      product: PRODUCT,
      version: "1",
      apiBaseUrl: `${BASE_URL}/v1`,
      capabilities: { accounts: true, profiles: true, devices: true, qrPairing: true, sync: true, liveTv: true }
    });
  }

  if (req.method === "POST" && url.pathname === "/v1/device-pairing") {
    const pairingId = randomId();
    const userCode = randomCode();
    const expiresAt = Date.now() + PAIRING_TTL * 1000;
    challenges.set(pairingId, { pairingId, userCodeHash: crypto.createHash("sha256").update(userCode).digest("hex"), expiresAt, approved: false, consumed: false });
    const approvalUrl = `${BASE_URL}/pair?pairing=${encodeURIComponent(pairingId)}&code=${encodeURIComponent(userCode)}`;
    return json(res, 201, { pairingId, userCode, approvalUrl, qrPayload: approvalUrl, expiresAt: new Date(expiresAt).toISOString(), pollIntervalSeconds: 3 });
  }

  const statusMatch = url.pathname.match(/^\/v1\/device-pairing\/([0-9a-f-]+)$/i);
  if (req.method === "GET" && statusMatch) {
    const challenge = challenges.get(statusMatch[1]);
    if (!challenge) return json(res, 404, { status: "expired_or_unknown" });
    if (!challenge.approved) return json(res, 200, { status: "pending", expiresAt: new Date(challenge.expiresAt).toISOString() });
    challenge.consumed = true;
    return json(res, 200, { status: "approved", session: { accessToken: crypto.randomBytes(32).toString("base64url"), tokenType: "Bearer", expiresIn: 3600 } });
  }

  return json(res, 404, { error: "not_found" });
});

server.listen(PORT, "0.0.0.0", () => console.log(`${PRODUCT} backend listening on ${PORT}`));
