// Durable persistence boundary for the production MEDIA•HUB backend.
// The bootstrap HTTP service currently uses in-memory stores; production adapters
// must implement this contract before a stable release is declared.
export class MediaHubStore {
  async createPairingChallenge(_challenge) { throw new Error("not implemented"); }
  async getPairingChallenge(_id) { throw new Error("not implemented"); }
  async consumePairingChallenge(_id) { throw new Error("not implemented"); }
  async createDeviceSession(_session) { throw new Error("not implemented"); }
  async revokeDeviceSession(_sessionId) { throw new Error("not implemented"); }
  async getProfiles(_accountId) { throw new Error("not implemented"); }
  async putProfiles(_accountId, _profiles) { throw new Error("not implemented"); }
  async getSyncState(_accountId) { throw new Error("not implemented"); }
  async putSyncState(_accountId, _state) { throw new Error("not implemented"); }
  async health() { throw new Error("not implemented"); }
}
