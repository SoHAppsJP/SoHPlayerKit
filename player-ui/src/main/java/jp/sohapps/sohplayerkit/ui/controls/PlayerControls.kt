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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.sohapps.sohplayerkit.core.model.PlaybackEndAction
import jp.sohapps.sohplayerkit.core.model.PlaybackVideoInfo
import jp.sohapps.sohplayerkit.core.model.PlayerAspectMode
import jp.sohapps.sohplayerkit.core.model.PlayerColorPreset

/**
 * Complete engine-neutral player control panel.
 *
 * The host owns playback state, persistence, engine operations, resources, and any application-
 * specific actions supplied through [topActions] and [supplementalContent]. This component owns
 * the shared panel scaffold and composition of transport and settings controls.
 */
@Composable
fun PlayerControls(
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    seekBackMs: Long,
    seekForwardMs: Long,
    menu: PlayerMenu,
    playbackSpeed: Float,
    playbackEndAction: PlaybackEndAction,
    aspectMode: PlayerAspectMode,
    colorPreset: PlayerColorPreset,
    rotationLocked: Boolean,
    avoidCutout: Boolean,
    videoWidth: Int,
    videoHeight: Int,
    videoRotationDegrees: Int?,
    videoPixelRatio: Float?,
    videoFrameRate: Float,
    playbackVideoInfo: PlaybackVideoInfo,
    customAspectWidthText: String,
    customAspectHeightText: String,
    colorBrightness: Float,
    colorContrast: Float,
    colorSaturation: Float,
    colorGamma: Float,
    colorTemperature: Float,
    transportIcons: PlayerTransportIcons,
    settingsIcons: PlayerSettingsIcons,
    onPreviousClick: () -> Unit,
    onSeekToStartClick: () -> Unit,
    onSeekBackClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeekForwardClick: () -> Unit,
    onSeekToEndClick: () -> Unit,
    onNextClick: () -> Unit,
    onSliderValueChange: (Long) -> Unit,
    onSliderValueChangeFinished: (Long) -> Unit,
    onMenuClick: (PlayerMenu) -> Unit,
    onPlaybackSpeedSelected: (Float) -> Unit,
    onPlaybackEndActionSelected: (PlaybackEndAction) -> Unit,
    onAspectModeSelected: (PlayerAspectMode) -> Unit,
    onColorPresetSelected: (PlayerColorPreset) -> Unit,
    onRotationLockClick: () -> Unit,
    onAvoidCutoutClick: () -> Unit,
    onCustomAspectWidthChange: (String) -> Unit,
    onCustomAspectHeightChange: (String) -> Unit,
    onColorBrightnessChange: (Float) -> Unit,
    onColorContrastChange: (Float) -> Unit,
    onColorSaturationChange: (Float) -> Unit,
    onColorGammaChange: (Float) -> Unit,
    onColorTemperatureChange: (Float) -> Unit,
    topActions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    supplementalContent: @Composable ColumnScope.() -> Unit = {}
) {
    PlayerControlPanel(
        modifier = modifier,
        topActions = topActions,
        supplementalContent = supplementalContent
    ) {
        PlayerTransportControls(
            isPlaying = isPlaying,
            currentPositionMs = currentPositionMs,
            durationMs = durationMs,
            seekBackMs = seekBackMs,
            seekForwardMs = seekForwardMs,
            icons = transportIcons,
            onPreviousClick = onPreviousClick,
            onSeekToStartClick = onSeekToStartClick,
            onSeekBackClick = onSeekBackClick,
            onPlayPauseClick = onPlayPauseClick,
            onSeekForwardClick = onSeekForwardClick,
            onSeekToEndClick = onSeekToEndClick,
            onNextClick = onNextClick,
            onSliderValueChange = onSliderValueChange,
            onSliderValueChangeFinished = onSliderValueChangeFinished,
            modifier = Modifier.fillMaxWidth()
        )
        PlayerSettingsControls(
            menu = menu,
            playbackSpeed = playbackSpeed,
            playbackEndAction = playbackEndAction,
            aspectMode = aspectMode,
            colorPreset = colorPreset,
            rotationLocked = rotationLocked,
            avoidCutout = avoidCutout,
            currentPositionMs = currentPositionMs,
            durationMs = durationMs,
            videoWidth = videoWidth,
            videoHeight = videoHeight,
            videoRotationDegrees = videoRotationDegrees,
            videoPixelRatio = videoPixelRatio,
            videoFrameRate = videoFrameRate,
            playbackVideoInfo = playbackVideoInfo,
            customAspectWidthText = customAspectWidthText,
            customAspectHeightText = customAspectHeightText,
            colorBrightness = colorBrightness,
            colorContrast = colorContrast,
            colorSaturation = colorSaturation,
            colorGamma = colorGamma,
            colorTemperature = colorTemperature,
            icons = settingsIcons,
            onMenuClick = onMenuClick,
            onPlaybackSpeedSelected = onPlaybackSpeedSelected,
            onPlaybackEndActionSelected = onPlaybackEndActionSelected,
            onAspectModeSelected = onAspectModeSelected,
            onColorPresetSelected = onColorPresetSelected,
            onRotationLockClick = onRotationLockClick,
            onAvoidCutoutClick = onAvoidCutoutClick,
            onCustomAspectWidthChange = onCustomAspectWidthChange,
            onCustomAspectHeightChange = onCustomAspectHeightChange,
            onColorBrightnessChange = onColorBrightnessChange,
            onColorContrastChange = onColorContrastChange,
            onColorSaturationChange = onColorSaturationChange,
            onColorGammaChange = onColorGammaChange,
            onColorTemperatureChange = onColorTemperatureChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
