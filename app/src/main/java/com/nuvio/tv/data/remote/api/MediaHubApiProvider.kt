package com.nuvio.tv.data.remote.api

/**
 * MEDIA•HUB cloud transport lives in com.nuvio.tv.mediahub.cloud.
 *
 * This package intentionally contains only provider/addon APIs inherited by the
 * playback stack. Keeping the MEDIA•HUB cloud boundary under mediahub.cloud
 * prevents a second networking/session implementation from drifting away from
 * MediaHubCloudApiClient and MediaHubSessionStore.
 */
internal object MediaHubApiProviderMigrationMarker
