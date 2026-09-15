import http from "node:http";
import crypto from "node:crypto";

const PORT = Number(process.env.PORT || 8080);
const BASE_URL = process.env.MEDIAHUB_PUBLIC_BASE_URL || `http://localhost:${PORT}`;
const PAIRING_TTL = Math.max(60, Number(process.env.MEDIAHUB_PAIRING_TTL_SECONDS || 300));
const PRODUCT = "MEDIA•HUB";
const challenges = new Map();
const sessions = new Map();
const sha256 = value => crypto.createHash("sha256").update(value).digest("hex");
const json = (res,status,body) => { res.writeHead(status,{"content-type":"application/json; charset=utf-8","cache-control":"no-store"}); res.end(JSON.stringify(body)); };
const bodyJson = req => new Promise((resolve,reject)=>{ let raw=""; req.on("data",c=>{raw+=c;if(raw.length>65536)req.destroy();}); req.on("end",()=>{try{resolve(raw?JSON.parse(raw):{});}catch(e){reject(e);}}); });
const safeEqual=(a,b)=>{const aa=Buffer.from(a),bb=Buffer.from(b);return aa.length===bb.length&&crypto.timingSafeEqual(aa,bb);};
const cleanExpired=()=>{const now=Date.now();for(const[id,v]of challenges)if(v.expiresAt<=now||v.consumed)challenges.delete(id);for(const[id,v]of sessions)if(v.expiresAt<=now)sessions.delete(id);};
const bearer=req=>String(req.headers.authorization||"").replace(/^Bearer\s+/i,"");
const authenticate=req=>{const token=bearer(req);if(!token)return null;const record=sessions.get(sha256(token));return record&&record.expiresAt>Date.now()?record:null;};

const server=http.createServer(async(req,res)=>{
  cleanExpired(); const url=new URL(req.url,BASE_URL);
  if(req.method==="GET"&&url.pathname==="/health")return json(res,200,{status:"ok",product:PRODUCT});
  if(req.method==="GET"&&url.pathname==="/.well-known/mediahub")return json(res,200,{product:PRODUCT,version:"1",apiBaseUrl:`${BASE_URL}/v1`,capabilities:{accounts:true,profiles:true,devices:true,qrPairing:true,sync:true,liveTv:true}});

  if(req.method==="POST"&&url.pathname==="/v1/device-pairing"){
    const pairingId=crypto.randomUUID(),userCode=crypto.randomBytes(4).toString("hex").toUpperCase(),deviceSecret=crypto.randomBytes(32).toString("base64url"),expiresAt=Date.now()+PAIRING_TTL*1000;
    let body={};try{body=await bodyJson(req);}catch{return json(res,400,{error:"invalid_json"});}
    challenges.set(pairingId,{pairingId,userCodeHash:sha256(userCode),deviceSecretHash:sha256(deviceSecret),expiresAt,approved:false,consumed:false,accountId:null,device:{id:String(body.deviceId||crypto.randomUUID()),name:String(body.deviceName||"MEDIA•HUB TV"),platform:String(body.platform||"android-tv")}});
    const approvalUrl=`${BASE_URL}/pair?pairing=${encodeURIComponent(pairingId)}&code=${encodeURIComponent(userCode)}`;
    return json(res,201,{pairingId,deviceSecret,userCode,approvalUrl,qrPayload:approvalUrl,expiresAt:new Date(expiresAt).toISOString(),pollIntervalSeconds:3});
  }

  const pairMatch=url.pathname.match(/^\/v1\/device-pairing\/([0-9a-f-]+)$/i);
  if(req.method==="GET"&&pairMatch){const c=challenges.get(pairMatch[1]);if(!c)return json(res,404,{status:"expired_or_unknown"});const secret=req.headers["x-mediahub-device-secret"]||"";if(!safeEqual(sha256(String(secret)),c.deviceSecretHash))return json(res,401,{error:"invalid_device_secret"});if(!c.approved)return json(res,200,{status:"pending",expiresAt:new Date(c.expiresAt).toISOString()});const accessToken=crypto.randomBytes(32).toString("base64url"),refreshToken=crypto.randomBytes(48).toString("base64url"),expiresAt=Date.now()+3600000;sessions.set(sha256(accessToken),{accountId:c.accountId,device:c.device,expiresAt});c.consumed=true;return json(res,200,{status:"approved",session:{accessToken,refreshToken,tokenType:"Bearer",expiresIn:3600},accountId:c.accountId,device:c.device});}

  const approveMatch=url.pathname.match(/^\/v1\/device-pairing\/([0-9a-f-]+)\/approve$/i);
  if(req.method==="POST"&&approveMatch){const c=challenges.get(approveMatch[1]);if(!c)return json(res,404,{error:"expired_or_unknown"});try{const body=await bodyJson(req);if(!safeEqual(sha256(String(body.userCode||"").toUpperCase()),c.userCodeHash))return json(res,401,{error:"invalid_code"});c.approved=true;c.accountId=String(body.accountId||"local-development-account");return json(res,200,{status:"approved"});}catch{return json(res,400,{error:"invalid_json"});}}

  if(req.method==="GET"&&url.pathname==="/v1/me"){const session=authenticate(req);if(!session)return json(res,401,{error:"unauthorized"});return json(res,200,{accountId:session.accountId,device:session.device});}
  return json(res,404,{error:"not_found"});
});
server.listen(PORT,"0.0.0.0",()=>console.log(`${PRODUCT} backend listening on ${PORT}`));
