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

package jp.sohapps.sohplayerkit.ui.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReusableContentHost
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import jp.sohapps.sohplayerkit.ui.playback.PlayerColorState
import jp.sohapps.sohplayerkit.ui.playback.PlayerDisplaySettingsState
import jp.sohapps.sohplayerkit.ui.playback.PlayerPlaybackSettingsState
import jp.sohapps.sohplayerkit.ui.playback.PlayerPlaybackState
import jp.sohapps.sohplayerkit.ui.playback.PlayerVideoMetadataState

/**
 * Binds the shared player UI state holders to [PlayerControls].
 *
 * Playback-engine operations stay outside this component. The host only owns the repeated
 * interaction behavior shared by ExoPlayer and LibVLC surfaces: refreshing control visibility,
 * clearing transient feedback before slider seeks, and wiring shared settings states.
 */
@Composable
fun PlayerControlsHost(
    controlsState: PlayerControlsVisibilityState,
    playbackState: PlayerPlaybackState,
    playbackSettingsState: PlayerPlaybackSettingsState,
    displayState: PlayerDisplaySettingsState,
    colorState: PlayerColorState,
    videoMetadata: PlayerVideoMetadataState,
    seekBackMs: Long,
    seekForwardMs: Long,
    transportIcons: PlayerTransportIcons,
    settingsIcons: PlayerSettingsIcons,
    onPreviousClick: () -> Unit,
    onSeekToStartClick: () -> Unit,
    onSeekBackClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeekForwardClick: () -> Unit,
    onSeekToEndClick: () -> Unit,
    onNextClick: () -> Unit,
    onSeek: (positionMs: Long, force: Boolean) -> Unit,
    onClearTransientFeedback: () -> Unit,
    topActions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    videoRotationDegrees: Int? = videoMetadata.rotationDegrees,
    videoPixelRatio: Float? = videoMetadata.pixelRatio,
    supplementalContent: @Composable ColumnScope.() -> Unit = {}
) {
    val visible = controlsState.visible
    val layoutWarmupState = remember { PlayerControlsLayoutWarmupState() }

    Box(
        modifier = modifier.retainedPlayerControlsVisibility(
            visible = visible,
            warmupState = layoutWarmupState
        )
    ) {
        ReusableContentHost(
            active = shouldKeepPlayerControlsContentActive(
                visible = visible,
                premeasured = layoutWarmupState.premeasured.value
            )
        ) {
            PlayerControls(
                modifier = Modifier.fillMaxWidth(),
                isPlaying = playbackState.isPlaying,
                currentPositionMs = playbackState.currentPositionMs,
                durationMs = playbackState.durationMs,
                seekBackMs = seekBackMs,
                seekForwardMs = seekForwardMs,
                menu = controlsState.menu,
                playbackSpeed = playbackSettingsState.playbackSpeed,
                playbackEndAction = playbackSettingsState.playbackEndAction,
                aspectMode = displayState.aspectMode,
                colorPreset = colorState.preset,
                rotationLocked = displayState.rotationLocked,
                avoidCutout = displayState.avoidCutout,
                videoWidth = videoMetadata.width,
                videoHeight = videoMetadata.height,
                videoRotationDegrees = videoRotationDegrees,
                videoPixelRatio = videoPixelRatio,
                videoFrameRate = videoMetadata.frameRate,
                playbackVideoInfo = videoMetadata.playbackVideoInfo,
                customAspectWidthText = displayState.customAspectWidthText,
                customAspectHeightText = displayState.customAspectHeightText,
                colorBrightness = colorState.brightness,
                colorContrast = colorState.contrast,
                colorSaturation = colorState.saturation,
                colorGamma = colorState.gamma,
                colorTemperature = colorState.temperature,
                transportIcons = transportIcons,
                settingsIcons = settingsIcons,
                onPreviousClick = {
                    performPlayerControlsInteraction(controlsState, onPreviousClick)
                },
                onSeekToStartClick = {
                    performPlayerControlsInteraction(controlsState, onSeekToStartClick)
                },
                onSeekBackClick = {
                    performPlayerControlsInteraction(controlsState, onSeekBackClick)
                },
                onPlayPauseClick = {
                    performPlayerControlsInteraction(controlsState, onPlayPauseClick)
                },
                onSeekForwardClick = {
                    performPlayerControlsInteraction(controlsState, onSeekForwardClick)
                },
                onSeekToEndClick = {
                    performPlayerControlsInteraction(controlsState, onSeekToEndClick)
                },
                onNextClick = {
                    performPlayerControlsInteraction(controlsState, onNextClick)
                },
                onSliderValueChange = { position ->
                    performPlayerControlsSeek(
                        controlsState = controlsState,
                        positionMs = position,
                        force = false,
                        onClearTransientFeedback = onClearTransientFeedback,
                        onSeek = onSeek
                    )
                },
                onSliderValueChangeFinished = { position ->
                    performPlayerControlsSeek(
                        controlsState = controlsState,
                        positionMs = position,
                        force = true,
                        onClearTransientFeedback = onClearTransientFeedback,
                        onSeek = onSeek
                    )
                },
                onMenuClick = controlsState::toggleMenu,
                onPlaybackSpeedSelected = playbackSettingsState::selectPlaybackSpeed,
                onPlaybackEndActionSelected = playbackSettingsState::selectPlaybackEndAction,
                onAspectModeSelected = displayState::selectAspectMode,
                onColorPresetSelected = colorState::selectPreset,
                onRotationLockClick = displayState::toggleRotationLocked,
                onAvoidCutoutClick = displayState::toggleAvoidCutout,
                onCustomAspectWidthChange = displayState::updateCustomAspectWidthText,
                onCustomAspectHeightChange = displayState::updateCustomAspectHeightText,
                onColorBrightnessChange = colorState::setCustomBrightness,
                onColorContrastChange = colorState::setCustomContrast,
                onColorSaturationChange = colorState::setCustomSaturation,
                onColorGammaChange = colorState::setCustomGamma,
                onColorTemperatureChange = colorState::setCustomTemperature,
                topActions = topActions,
                supplementalContent = supplementalContent
            )
        }
    }
}

private class PlayerControlsLayoutWarmupState {
    val premeasured = mutableStateOf(false)
}

private fun Modifier.retainedPlayerControlsVisibility(
    visible: Boolean,
    warmupState: PlayerControlsLayoutWarmupState
): Modifier {
    return layout { measurable, constraints ->
        if (!visible) {
            if (shouldPremeasurePlayerControls(
                    visible = false,
                    premeasured = warmupState.premeasured.value
                )
            ) {
                // Warm the complete panel layout once while it is still invisible. Measuring the
                // child here executes the expensive initial layout work, but the 0x0 parent below
                // keeps it off-screen and non-interactive until the user opens the controls.
                measurable.measure(constraints)
                warmupState.premeasured.value = true
            }
            layout(0, 0) {}
        } else {
            val placeable = measurable.measure(constraints)
            warmupState.premeasured.value = true
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
    }
}

internal fun shouldKeepPlayerControlsContentActive(
    visible: Boolean,
    premeasured: Boolean
): Boolean {
    return visible || !premeasured
}

internal fun shouldPremeasurePlayerControls(
    visible: Boolean,
    premeasured: Boolean
): Boolean {
    return !visible && !premeasured
}

internal fun performPlayerControlsInteraction(
    controlsState: PlayerControlsVisibilityState,
    action: () -> Unit
) {
    controlsState.show()
    action()
}

internal fun performPlayerControlsSeek(
    controlsState: PlayerControlsVisibilityState,
    positionMs: Long,
    force: Boolean,
    onClearTransientFeedback: () -> Unit,
    onSeek: (Long, Boolean) -> Unit
) {
    controlsState.show()
    onClearTransientFeedback()
    onSeek(positionMs, force)
}
