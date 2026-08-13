/*
 * Copyright 2026 SoH Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.sohapps.sohplayerkit.ui.playback

import android.content.Context
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import jp.sohapps.sohplayerkit.core.model.PlaybackEndAction
import jp.sohapps.sohplayerkit.core.model.PlaybackVideoInfo
import jp.sohapps.sohplayerkit.core.model.PlayerAspectMode
import jp.sohapps.sohplayerkit.core.model.PlayerColorPreset
import jp.sohapps.sohplayerkit.core.seek.PlayerSeekGestureState
import jp.sohapps.sohplayerkit.ui.controls.PlayerControlsVisibilityState
import jp.sohapps.sohplayerkit.ui.controls.rememberPlayerControlsVisibilityState
import jp.sohapps.sohplayerkit.ui.gesture.PlayerGestureActionController
import jp.sohapps.sohplayerkit.ui.gesture.PlayerGestureFeedbackState
import jp.sohapps.sohplayerkit.ui.gesture.PlayerZoomState
import jp.sohapps.sohplayerkit.ui.gesture.rememberPlayerGestureActionController
import jp.sohapps.sohplayerkit.ui.gesture.rememberPlayerGestureFeedbackState
import jp.sohapps.sohplayerkit.ui.gesture.rememberPlayerZoomState

/** Keys controlling the lifetime of engine-dependent parts of [PlayerUiState]. */
data class PlayerUiStateKeys(
    val playback: Any? = Unit,
    val playbackSettings: Any? = Unit,
    val displaySettings: Any? = Unit,
    val colorSettings: Any? = Unit,
    val videoMetadata: Any? = Unit,
    val seek: Any? = Unit
)

/** Initial values used when the corresponding state object is first remembered. */
data class PlayerUiInitialValues(
    val playbackSpeed: Float,
    val playbackEndAction: PlaybackEndAction,
    val aspectMode: PlayerAspectMode,
    val customAspectWidth: Float,
    val customAspectHeight: Float,
    val rotationLocked: Boolean,
    val avoidCutout: Boolean,
    val colorPreset: PlayerColorPreset,
    val colorValues: PlayerColorValues,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val videoWidth: Int = 0,
    val videoHeight: Int = 0,
    val videoRotationDegrees: Int? = null,
    val videoPixelRatio: Float? = null,
    val videoFrameRate: Float = 0.0f,
    val playbackVideoInfo: PlaybackVideoInfo = PlaybackVideoInfo()
)

/**
 * Engine-neutral collection of player UI state holders shared by ExoPlayer and LibVLC hosts.
 *
 * The individual state classes remain independently keyed so hosts can preserve settings while
 * replacing only playback- or media-specific state. Playback engines and application persistence
 * stay outside the library and are supplied through callbacks.
 */
@Stable
class PlayerUiState internal constructor(
    val controlsState: PlayerControlsVisibilityState,
    val playbackState: PlayerPlaybackState,
    val playbackSettingsState: PlayerPlaybackSettingsState,
    val displayState: PlayerDisplaySettingsState,
    val colorState: PlayerColorState,
    val videoMetadata: PlayerVideoMetadataState,
    val statusState: PlayerStatusState,
    val gestureFeedbackState: PlayerGestureFeedbackState,
    val gestureActionController: PlayerGestureActionController,
    val zoomState: PlayerZoomState,
    val seekGestureState: PlayerSeekGestureState
) {
    fun hideControlsAndFeedback() {
        controlsState.hide()
        gestureFeedbackState.clear()
    }
}

/**
 * Creates and wires the common player UI states without depending on a playback engine or an
 * application settings repository.
 */
@Composable
fun rememberPlayerUiState(
    context: Context,
    window: Window?,
    keys: PlayerUiStateKeys,
    initialValues: PlayerUiInitialValues,
    bindings: PlayerUiStateBindings
): PlayerUiState {
    val seekScope = rememberCoroutineScope()
    val controlsState = rememberPlayerControlsVisibilityState()
    val playbackState = rememberPlayerPlaybackState(
        key = keys.playback,
        initialIsPlaying = initialValues.isPlaying,
        initialPositionMs = initialValues.currentPositionMs,
        initialDurationMs = initialValues.durationMs
    )
    val playbackSettingsState = rememberPlayerPlaybackSettingsState(
        key = keys.playbackSettings,
        initialPlaybackSpeed = initialValues.playbackSpeed,
        initialPlaybackEndAction = initialValues.playbackEndAction,
        onInteraction = controlsState::show,
        onPlaybackSpeedChanged = bindings.onPlaybackSpeedChanged,
        onPlaybackEndActionChanged = bindings.onPlaybackEndActionChanged
    )
    val displayState = rememberPlayerDisplaySettingsState(
        key = keys.displaySettings,
        initialAspectMode = initialValues.aspectMode,
        initialCustomAspectWidth = initialValues.customAspectWidth,
        initialCustomAspectHeight = initialValues.customAspectHeight,
        initialRotationLocked = initialValues.rotationLocked,
        initialAvoidCutout = initialValues.avoidCutout,
        onInteraction = controlsState::show,
        onAspectModeChanged = bindings.onAspectModeChanged,
        onCustomAspectWidthChanged = bindings.onCustomAspectWidthChanged,
        onCustomAspectHeightChanged = bindings.onCustomAspectHeightChanged,
        onRotationLockedChanged = bindings.onRotationLockedChanged,
        onAvoidCutoutChanged = bindings.onAvoidCutoutChanged
    )
    val colorState = rememberPlayerColorState(
        key = keys.colorSettings,
        initialPreset = initialValues.colorPreset,
        initialValues = initialValues.colorValues,
        onInteraction = controlsState::show,
        onPresetChanged = bindings.onColorPresetChanged,
        readValues = bindings.readColorValues ?: { initialValues.colorValues },
        onBrightnessChanged = bindings.onColorBrightnessChanged,
        onContrastChanged = bindings.onColorContrastChanged,
        onSaturationChanged = bindings.onColorSaturationChanged,
        onGammaChanged = bindings.onColorGammaChanged,
        onTemperatureChanged = bindings.onColorTemperatureChanged
    )
    val videoMetadata = rememberPlayerVideoMetadataState(
        key = keys.videoMetadata,
        initialWidth = initialValues.videoWidth,
        initialHeight = initialValues.videoHeight,
        initialRotationDegrees = initialValues.videoRotationDegrees,
        initialPixelRatio = initialValues.videoPixelRatio,
        initialFrameRate = initialValues.videoFrameRate,
        initialPlaybackVideoInfo = initialValues.playbackVideoInfo
    )
    val statusState = rememberPlayerStatusState()
    val gestureFeedbackState = rememberPlayerGestureFeedbackState()
    val gestureActionController = rememberPlayerGestureActionController(
        context = context,
        window = window,
        playbackSpeed = { playbackSettingsState.playbackSpeed },
        onPlaybackSpeedChanged = playbackSettingsState::updatePlaybackSpeed,
        feedbackState = gestureFeedbackState
    )
    val zoomState = rememberPlayerZoomState()

    val currentPlaybackState = rememberUpdatedState(playbackState)
    val currentPlaybackSettingsState = rememberUpdatedState(playbackSettingsState)
    val currentGestureFeedbackState = rememberUpdatedState(gestureFeedbackState)
    val currentSeekAction = rememberUpdatedState(bindings.seekAction)
    val seekGestureState = remember(keys.seek) {
        PlayerSeekGestureState(
            scope = seekScope,
            currentPositionMs = { currentPlaybackState.value.currentPositionMs },
            durationMs = { currentPlaybackState.value.durationMs },
            playbackSpeed = { currentPlaybackSettingsState.value.playbackSpeed },
            onPreviewPositionChanged = { position ->
                currentPlaybackState.value.updatePosition(position)
            },
            onSeekFeedback = { position, duration ->
                currentGestureFeedbackState.value.showSeek(position, duration)
            },
            seekAction = { position -> currentSeekAction.value(position) }
        )
    }

    return remember(
        controlsState,
        playbackState,
        playbackSettingsState,
        displayState,
        colorState,
        videoMetadata,
        statusState,
        gestureFeedbackState,
        gestureActionController,
        zoomState,
        seekGestureState
    ) {
        PlayerUiState(
            controlsState = controlsState,
            playbackState = playbackState,
            playbackSettingsState = playbackSettingsState,
            displayState = displayState,
            colorState = colorState,
            videoMetadata = videoMetadata,
            statusState = statusState,
            gestureFeedbackState = gestureFeedbackState,
            gestureActionController = gestureActionController,
            zoomState = zoomState,
            seekGestureState = seekGestureState
        )
    }
}
