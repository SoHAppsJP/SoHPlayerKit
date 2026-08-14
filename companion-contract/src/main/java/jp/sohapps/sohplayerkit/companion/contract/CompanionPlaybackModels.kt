package jp.sohapps.sohplayerkit.companion.contract

import android.net.Uri

data class CompanionPlaybackRequest(
    val mediaUri: Uri,
    val mimeType: String,
    val displayName: String,
    val resumePositionMs: Long = 0L,
    val durationMs: Long? = null,
    val videoWidth: Int? = null,
    val videoHeight: Int? = null,
    val videoFps: Float? = null
) {
    init {
        require(mimeType.isNotBlank()) { "mimeType must not be blank" }
        require(displayName.isNotBlank()) { "displayName must not be blank" }
        require(resumePositionMs >= 0L) { "resumePositionMs must be non-negative" }
        require(durationMs == null || durationMs > 0L) { "durationMs must be positive when specified" }
        require(videoWidth == null || videoWidth > 0) { "videoWidth must be positive when specified" }
        require(videoHeight == null || videoHeight > 0) { "videoHeight must be positive when specified" }
        require(videoFps == null || (videoFps.isFinite() && videoFps > 0f)) {
            "videoFps must be finite and positive when specified"
        }
    }
}

data class CompanionPlaybackResult(
    val positionMs: Long,
    val durationMs: Long? = null
) {
    init {
        require(positionMs >= 0L) { "positionMs must be non-negative" }
        require(durationMs == null || durationMs > 0L) { "durationMs must be positive when specified" }
    }
}
