package jp.sohapps.sohplayerkit.ui.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import jp.sohapps.sohplayerkit.core.model.PlaybackEndAction

@Stable
class PlayerPlaybackSettingsState(
    initialPlaybackSpeed: Float,
    initialPlaybackEndAction: PlaybackEndAction,
    private val onInteraction: () -> Unit = {},
    private val onPlaybackSpeedChanged: (Float) -> Unit = {},
    private val onPlaybackEndActionChanged: (PlaybackEndAction) -> Unit = {}
) {
    var playbackSpeed by mutableFloatStateOf(initialPlaybackSpeed)
        private set

    var playbackEndAction by mutableStateOf(initialPlaybackEndAction)
        private set

    /**
     * Applies a speed change without opening or extending the controls overlay.
     *
     * This is intended for gesture-driven speed changes, where the existing player UI only
     * shows gesture feedback and does not make the controls panel visible.
     */
    fun updatePlaybackSpeed(speed: Float) {
        playbackSpeed = speed
        onPlaybackSpeedChanged(speed)
    }

    /** Applies a speed selected from the controls panel and extends its visible lifetime. */
    fun selectPlaybackSpeed(speed: Float) {
        onInteraction()
        updatePlaybackSpeed(speed)
    }

    /** Applies the end-of-playback action selected from the controls panel. */
    fun selectPlaybackEndAction(action: PlaybackEndAction) {
        onInteraction()
        playbackEndAction = action
        onPlaybackEndActionChanged(action)
    }
}

@Composable
fun rememberPlayerPlaybackSettingsState(
    key: Any? = Unit,
    initialPlaybackSpeed: Float,
    initialPlaybackEndAction: PlaybackEndAction,
    onInteraction: () -> Unit = {},
    onPlaybackSpeedChanged: (Float) -> Unit = {},
    onPlaybackEndActionChanged: (PlaybackEndAction) -> Unit = {}
): PlayerPlaybackSettingsState {
    return remember(key) {
        PlayerPlaybackSettingsState(
            initialPlaybackSpeed = initialPlaybackSpeed,
            initialPlaybackEndAction = initialPlaybackEndAction,
            onInteraction = onInteraction,
            onPlaybackSpeedChanged = onPlaybackSpeedChanged,
            onPlaybackEndActionChanged = onPlaybackEndActionChanged
        )
    }
}
