import http from "node:http";
import crypto from "node:crypto";

const PORT = Number(process.env.PORT || 8080);
const BASE_URL = process.env.MEDIAHUB_PUBLIC_BASE_URL || `http://localhost:${PORT}`;
const PAIRING_TTL = Math.max(60, Number(process.env.MEDIAHUB_PAIRING_TTL_SECONDS || 300));
const PRODUCT = "MEDIA•HUB";
const challenges = new Map();
const sha256 = value => crypto.createHash("sha256").update(value).digest("hex");
const json = (res, status, body) => { res.writeHead(status, { "content-type": "application/json; charset=utf-8", "cache-control": "no-store" }); res.end(JSON.stringify(body)); };
const bodyJson = req => new Promise((resolve, reject) => { let raw=""; req.on("data", c => { raw += c; if (raw.length > 16384) req.destroy(); }); req.on("end", () => { try { resolve(raw ? JSON.parse(raw) : {}); } catch (e) { reject(e); } }); });
const cleanExpired = () => { const now=Date.now(); for (const [id,v] of challenges) if (v.expiresAt <= now || v.consumed) challenges.delete(id); };
const safeEqual = (a,b) => { const aa=Buffer.from(a); const bb=Buffer.from(b); return aa.length === bb.length && crypto.timingSafeEqual(aa,bb); };

const server = http.createServer(async (req, res) => {
  cleanExpired();
  const url = new URL(req.url, BASE_URL);
  if (req.method === "GET" && url.pathname === "/health") return json(res,200,{status:"ok",product:PRODUCT});
  if (req.method === "GET" && url.pathname === "/.well-known/mediahub") return json(res,200,{product:PRODUCT,version:"1",apiBaseUrl:`${BASE_URL}/v1`,capabilities:{accounts:true,profiles:true,devices:true,qrPairing:true,sync:true,liveTv:true}});

  if (req.method === "POST" && url.pathname === "/v1/device-pairing") {
    const pairingId=crypto.randomUUID(); const userCode=crypto.randomBytes(4).toString("hex").toUpperCase(); const deviceSecret=crypto.randomBytes(32).toString("base64url"); const expiresAt=Date.now()+PAIRING_TTL*1000;
    challenges.set(pairingId,{pairingId,userCodeHash:sha256(userCode),deviceSecretHash:sha256(deviceSecret),expiresAt,approved:false,consumed:false,accountId:null});
    const approvalUrl=`${BASE_URL}/pair?pairing=${encodeURIComponent(pairingId)}&code=${encodeURIComponent(userCode)}`;
    return json(res,201,{pairingId,deviceSecret,userCode,approvalUrl,qrPayload:approvalUrl,expiresAt:new Date(expiresAt).toISOString(),pollIntervalSeconds:3});
  }

  const pairMatch=url.pathname.match(/^\/v1\/device-pairing\/([0-9a-f-]+)$/i);
  if (req.method === "GET" && pairMatch) {
    const challenge=challenges.get(pairMatch[1]); if (!challenge) return json(res,404,{status:"expired_or_unknown"});
    const secret=req.headers["x-mediahub-device-secret"] || ""; if (!safeEqual(sha256(String(secret)),challenge.deviceSecretHash)) return json(res,401,{error:"invalid_device_secret"});
    if (!challenge.approved) return json(res,200,{status:"pending",expiresAt:new Date(challenge.expiresAt).toISOString()});
    challenge.consumed=true; return json(res,200,{status:"approved",session:{accessToken:crypto.randomBytes(32).toString("base64url"),refreshToken:crypto.randomBytes(48).toString("base64url"),tokenType:"Bearer",expiresIn:3600},accountId:challenge.accountId});
  }

  const approveMatch=url.pathname.match(/^\/v1\/device-pairing\/([0-9a-f-]+)\/approve$/i);
  if (req.method === "POST" && approveMatch) {
    const challenge=challenges.get(approveMatch[1]); if (!challenge) return json(res,404,{error:"expired_or_unknown"});
    try { const body=await bodyJson(req); if (!safeEqual(sha256(String(body.userCode || "").toUpperCase()),challenge.userCodeHash)) return json(res,401,{error:"invalid_code"}); challenge.approved=true; challenge.accountId=String(body.accountId || "local-development-account"); return json(res,200,{status:"approved"}); } catch { return json(res,400,{error:"invalid_json"}); }
  }

  return json(res,404,{error:"not_found"});
});
server.listen(PORT,"0.0.0.0",()=>console.log(`${PRODUCT} backend listening on ${PORT}`));
