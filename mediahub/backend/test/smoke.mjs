import { spawn } from "node:child_process";
import assert from "node:assert/strict";
const port=18080,base=`http://127.0.0.1:${port}`;
const child=spawn(process.execPath,["src/server.js"],{cwd:new URL("..",import.meta.url),env:{...process.env,PORT:String(port),MEDIAHUB_PUBLIC_BASE_URL:base},stdio:"inherit"});
const sleep=ms=>new Promise(r=>setTimeout(r,ms));
try{
 await sleep(400);
 const health=await fetch(`${base}/health`).then(r=>r.json());assert.equal(health.product,"MEDIA•HUB");
 const discovery=await fetch(`${base}/.well-known/mediahub`).then(r=>r.json());assert.equal(discovery.capabilities.qrPairing,true);
 const created=await fetch(`${base}/v1/device-pairing`,{method:"POST",headers:{"content-type":"application/json"},body:JSON.stringify({deviceId:"test-tv",deviceName:"Living Room",platform:"android-tv"})}).then(r=>r.json());assert.ok(created.pairingId&&created.deviceSecret&&created.userCode);
 const unauthorized=await fetch(`${base}/v1/device-pairing/${created.pairingId}`).then(r=>r.status);assert.equal(unauthorized,401);
 const pending=await fetch(`${base}/v1/device-pairing/${created.pairingId}`,{headers:{"x-mediahub-device-secret":created.deviceSecret}}).then(r=>r.json());assert.equal(pending.status,"pending");
 const approved=await fetch(`${base}/v1/device-pairing/${created.pairingId}/approve`,{method:"POST",headers:{"content-type":"application/json"},body:JSON.stringify({userCode:created.userCode,accountId:"smoke-account"})}).then(r=>r.json());assert.equal(approved.status,"approved");
 const session=await fetch(`${base}/v1/device-pairing/${created.pairingId}`,{headers:{"x-mediahub-device-secret":created.deviceSecret}}).then(r=>r.json());assert.equal(session.status,"approved");assert.ok(session.session.accessToken&&session.session.refreshToken);assert.equal(session.device.id,"test-tv");
 const me=await fetch(`${base}/v1/me`,{headers:{authorization:`Bearer ${session.session.accessToken}`}}).then(r=>r.json());assert.equal(me.accountId,"smoke-account");assert.equal(me.device.name,"Living Room");
 console.log("MEDIA•HUB backend smoke test passed");
}finally{child.kill();}
