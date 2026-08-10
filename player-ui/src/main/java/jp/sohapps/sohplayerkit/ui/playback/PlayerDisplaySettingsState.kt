package jp.sohapps.sohplayerkit.ui.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import jp.sohapps.sohplayerkit.core.model.PlayerAspectMode

@Stable
class PlayerDisplaySettingsState(
    initialAspectMode: PlayerAspectMode,
    initialCustomAspectWidth: Float,
    initialCustomAspectHeight: Float,
    initialRotationLocked: Boolean,
    initialAvoidCutout: Boolean,
    private val onInteraction: () -> Unit = {},
    private val onAspectModeChanged: (PlayerAspectMode) -> Unit = {},
    private val onCustomAspectWidthChanged: (Float) -> Unit = {},
    private val onCustomAspectHeightChanged: (Float) -> Unit = {},
    private val onRotationLockedChanged: (Boolean) -> Unit = {},
    private val onAvoidCutoutChanged: (Boolean) -> Unit = {}
) {
    var aspectMode by mutableStateOf(initialAspectMode)
        private set

    var customAspectWidthText by mutableStateOf(initialCustomAspectWidth.toString())
        private set

    var customAspectHeightText by mutableStateOf(initialCustomAspectHeight.toString())
        private set

    var customAspectWidth by mutableFloatStateOf(initialCustomAspectWidth)
        private set

    var customAspectHeight by mutableFloatStateOf(initialCustomAspectHeight)
        private set

    var rotationLocked by mutableStateOf(initialRotationLocked)
        private set

    var avoidCutout by mutableStateOf(initialAvoidCutout)
        private set

    fun selectAspectMode(mode: PlayerAspectMode) {
        onInteraction()
        applyAspectMode(mode)
    }

    fun updateCustomAspectWidthText(value: String) {
        onInteraction()
        customAspectWidthText = value
        val width = value.toFloatOrNull()
        if (width != null && width > 0f) {
            customAspectWidth = width
            onCustomAspectWidthChanged(width)
            applyAspectMode(PlayerAspectMode.CUSTOM)
        }
    }

    fun updateCustomAspectHeightText(value: String) {
        onInteraction()
        customAspectHeightText = value
        val height = value.toFloatOrNull()
        if (height != null && height > 0f) {
            customAspectHeight = height
            onCustomAspectHeightChanged(height)
            applyAspectMode(PlayerAspectMode.CUSTOM)
        }
    }

    fun toggleRotationLocked() {
        onInteraction()
        val nextValue = !rotationLocked
        rotationLocked = nextValue
        onRotationLockedChanged(nextValue)
    }

    fun toggleAvoidCutout() {
        onInteraction()
        val nextValue = !avoidCutout
        avoidCutout = nextValue
        onAvoidCutoutChanged(nextValue)
    }

    private fun applyAspectMode(mode: PlayerAspectMode) {
        aspectMode = mode
        onAspectModeChanged(mode)
    }
}

@Composable
fun rememberPlayerDisplaySettingsState(
    key: Any? = Unit,
    initialAspectMode: PlayerAspectMode,
    initialCustomAspectWidth: Float,
    initialCustomAspectHeight: Float,
    initialRotationLocked: Boolean,
    initialAvoidCutout: Boolean,
    onInteraction: () -> Unit = {},
    onAspectModeChanged: (PlayerAspectMode) -> Unit = {},
    onCustomAspectWidthChanged: (Float) -> Unit = {},
    onCustomAspectHeightChanged: (Float) -> Unit = {},
    onRotationLockedChanged: (Boolean) -> Unit = {},
    onAvoidCutoutChanged: (Boolean) -> Unit = {}
): PlayerDisplaySettingsState {
    return remember(key) {
        PlayerDisplaySettingsState(
            initialAspectMode = initialAspectMode,
            initialCustomAspectWidth = initialCustomAspectWidth,
            initialCustomAspectHeight = initialCustomAspectHeight,
            initialRotationLocked = initialRotationLocked,
            initialAvoidCutout = initialAvoidCutout,
            onInteraction = onInteraction,
            onAspectModeChanged = onAspectModeChanged,
            onCustomAspectWidthChanged = onCustomAspectWidthChanged,
            onCustomAspectHeightChanged = onCustomAspectHeightChanged,
            onRotationLockedChanged = onRotationLockedChanged,
            onAvoidCutoutChanged = onAvoidCutoutChanged
        )
    }
}
