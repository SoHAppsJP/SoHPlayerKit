package jp.sohapps.sohplayerkit.companion.contract

import android.net.Uri

data class CompanionPlayerSettingsSnapshot(
    val buttonSeekBackMs: Long,
    val buttonSeekForwardMs: Long,
    val doubleTapSeekBackMs: Long,
    val doubleTapSeekForwardMs: Long,
    val controlsAutoHideMs: Long,
    val playbackSpeed: Float,
    val playbackEndAction: String,
    val aspectMode: String,
    val customAspectWidth: Float,
    val customAspectHeight: Float,
    val rotationLocked: Boolean,
    val avoidCutout: Boolean,
    val colorPreset: String,
    val colorBrightness: Float,
    val colorContrast: Float,
    val colorSaturation: Float,
    val colorGamma: Float,
    val colorTemperature: Float
) {
    init {
        require(buttonSeekBackMs >= 0L) { "buttonSeekBackMs must be non-negative" }
        require(buttonSeekForwardMs >= 0L) { "buttonSeekForwardMs must be non-negative" }
        require(doubleTapSeekBackMs >= 0L) { "doubleTapSeekBackMs must be non-negative" }
        require(doubleTapSeekForwardMs >= 0L) { "doubleTapSeekForwardMs must be non-negative" }
        require(controlsAutoHideMs >= 0L) { "controlsAutoHideMs must be non-negative" }
        require(playbackSpeed.isFinite() && playbackSpeed > 0f) {
            "playbackSpeed must be finite and positive"
        }
        require(playbackEndAction.isNotBlank()) { "playbackEndAction must not be blank" }
        require(aspectMode.isNotBlank()) { "aspectMode must not be blank" }
        require(customAspectWidth.isFinite() && customAspectWidth > 0f) {
            "customAspectWidth must be finite and positive"
        }
        require(customAspectHeight.isFinite() && customAspectHeight > 0f) {
            "customAspectHeight must be finite and positive"
        }
        require(colorPreset.isNotBlank()) { "colorPreset must not be blank" }
        require(colorBrightness.isFinite()) { "colorBrightness must be finite" }
        require(colorContrast.isFinite()) { "colorContrast must be finite" }
        require(colorSaturation.isFinite()) { "colorSaturation must be finite" }
        require(colorGamma.isFinite()) { "colorGamma must be finite" }
        require(colorTemperature.isFinite()) { "colorTemperature must be finite" }
    }
}

data class CompanionPlaybackRequest(
    val mediaUri: Uri,
    val mimeType: String,
    val displayName: String,
    val resumePositionMs: Long = 0L,
    val durationMs: Long? = null,
    val videoWidth: Int? = null,
    val videoHeight: Int? = null,
    val videoFps: Float? = null,
    val canNavigatePrevious: Boolean = false,
    val canNavigateNext: Boolean = false,
    val playerSettings: CompanionPlayerSettingsSnapshot? = null
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

enum class CompanionPlaybackResultAction {
    RETURN_TO_LIST,
    PREVIOUS,
    NEXT
}

data class CompanionPlaybackResult(
    val positionMs: Long,
    val durationMs: Long? = null,
    val action: CompanionPlaybackResultAction = CompanionPlaybackResultAction.RETURN_TO_LIST,
    val playerSettings: CompanionPlayerSettingsSnapshot? = null
) {
    init {
        require(positionMs >= 0L) { "positionMs must be non-negative" }
        require(durationMs == null || durationMs > 0L) { "durationMs must be positive when specified" }
    }
}
