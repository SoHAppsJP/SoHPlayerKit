package jp.sohapps.sohplayerkit.ui.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import jp.sohapps.sohplayerkit.core.model.PlaybackVideoInfo

@Stable
class PlayerVideoMetadataState(
    initialWidth: Int = 0,
    initialHeight: Int = 0,
    initialRotationDegrees: Int? = null,
    initialPixelRatio: Float? = null,
    initialFrameRate: Float = 0.0f,
    initialPlaybackVideoInfo: PlaybackVideoInfo = PlaybackVideoInfo()
) {
    var width by mutableStateOf(initialWidth)
        private set

    var height by mutableStateOf(initialHeight)
        private set

    var rotationDegrees by mutableStateOf(initialRotationDegrees)
        private set

    var pixelRatio by mutableStateOf(initialPixelRatio)
        private set

    var frameRate by mutableStateOf(initialFrameRate)
        private set

    var playbackVideoInfo by mutableStateOf(initialPlaybackVideoInfo)
        private set

    val hasVideoSize: Boolean
        get() = width > 0 && height > 0

    val rotationDegreesOrZero: Int
        get() = rotationDegrees ?: 0

    val pixelRatioOrOne: Float
        get() = pixelRatio ?: 1.0f

    fun replace(
        width: Int,
        height: Int,
        rotationDegrees: Int?,
        pixelRatio: Float?,
        frameRate: Float,
        playbackVideoInfo: PlaybackVideoInfo
    ) {
        this.width = width
        this.height = height
        this.rotationDegrees = rotationDegrees
        this.pixelRatio = pixelRatio
        this.frameRate = frameRate
        this.playbackVideoInfo = playbackVideoInfo
    }

    fun mergeKnown(
        width: Int,
        height: Int,
        rotationDegrees: Int?,
        pixelRatio: Float?,
        frameRate: Float,
        playbackVideoInfo: PlaybackVideoInfo
    ) {
        if (width > 0) {
            this.width = width
        }
        if (height > 0) {
            this.height = height
        }
        if (rotationDegrees != null) {
            this.rotationDegrees = rotationDegrees
        }
        if (pixelRatio != null && pixelRatio.isFinite() && pixelRatio > 0.0f) {
            this.pixelRatio = pixelRatio
        }
        if (frameRate.isFinite() && frameRate > 0.0f) {
            this.frameRate = frameRate
        }
        this.playbackVideoInfo = playbackVideoInfo
    }
}

@Composable
fun rememberPlayerVideoMetadataState(
    key: Any? = Unit,
    initialWidth: Int = 0,
    initialHeight: Int = 0,
    initialRotationDegrees: Int? = null,
    initialPixelRatio: Float? = null,
    initialFrameRate: Float = 0.0f,
    initialPlaybackVideoInfo: PlaybackVideoInfo = PlaybackVideoInfo()
): PlayerVideoMetadataState {
    return remember(key) {
        PlayerVideoMetadataState(
            initialWidth = initialWidth,
            initialHeight = initialHeight,
            initialRotationDegrees = initialRotationDegrees,
            initialPixelRatio = initialPixelRatio,
            initialFrameRate = initialFrameRate,
            initialPlaybackVideoInfo = initialPlaybackVideoInfo
        )
    }
}
