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

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.sohapps.sohplayerkit.core.seek.PlayerSeekGestureState
import jp.sohapps.sohplayerkit.ui.controls.PlayerControlsHost
import jp.sohapps.sohplayerkit.ui.controls.PlayerControlsVisibilityState
import jp.sohapps.sohplayerkit.ui.controls.PlayerSettingsIcons
import jp.sohapps.sohplayerkit.ui.controls.PlayerTransportIcons
import jp.sohapps.sohplayerkit.ui.gesture.PlayerGestureActionController
import jp.sohapps.sohplayerkit.ui.gesture.PlayerGestureFeedbackState
import jp.sohapps.sohplayerkit.ui.gesture.PlayerZoomState

/**
 * Shared player interaction and controls stack.
 *
 * This host owns no playback-engine state. ExoPlayer and LibVLC implementations provide their
 * engine-specific transport and double-tap callbacks while the common gesture overlay, status
 * overlay, control binding, seek preview behavior, and control-panel composition stay in one
 * reusable place.
 */
@Composable
fun BoxScope.PlayerUiHost(
    controlsState: PlayerControlsVisibilityState,
    playbackState: PlayerPlaybackState,
    playbackSettingsState: PlayerPlaybackSettingsState,
    displayState: PlayerDisplaySettingsState,
    colorState: PlayerColorState,
    videoMetadata: PlayerVideoMetadataState,
    zoomState: PlayerZoomState,
    gestureFeedbackState: PlayerGestureFeedbackState,
    gestureActionController: PlayerGestureActionController,
    seekGestureState: PlayerSeekGestureState,
    statusState: PlayerStatusState,
    seekBackMs: Long,
    seekForwardMs: Long,
    transportIcons: PlayerTransportIcons,
    settingsIcons: PlayerSettingsIcons,
    onDoubleTapLeft: (Int) -> Unit,
    onDoubleTapCenter: () -> Unit,
    onDoubleTapRight: (Int) -> Unit,
    onPreviousClick: () -> Unit,
    onSeekToStartClick: () -> Unit,
    onSeekBackClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeekForwardClick: () -> Unit,
    onSeekToEndClick: () -> Unit,
    onNextClick: () -> Unit,
    controlsModifier: Modifier = Modifier,
    gestureEnabled: Boolean = true,
    videoRotationDegrees: Int? = videoMetadata.rotationDegrees,
    videoPixelRatio: Float? = videoMetadata.pixelRatio,
    topActions: @Composable RowScope.() -> Unit = {},
    supplementalContent: @Composable ColumnScope.() -> Unit = {}
) {
    PlayerInteractionOverlayHost(
        controlsState = controlsState,
        zoomState = zoomState,
        gestureFeedbackState = gestureFeedbackState,
        gestureActionController = gestureActionController,
        seekGestureState = seekGestureState,
        statusState = statusState,
        onDoubleTapLeft = onDoubleTapLeft,
        onDoubleTapCenter = onDoubleTapCenter,
        onDoubleTapRight = onDoubleTapRight,
        gestureEnabled = gestureEnabled
    ) {
        PlayerControlsHost(
            controlsState = controlsState,
            playbackState = playbackState,
            playbackSettingsState = playbackSettingsState,
            displayState = displayState,
            colorState = colorState,
            videoMetadata = videoMetadata,
            seekBackMs = seekBackMs,
            seekForwardMs = seekForwardMs,
            transportIcons = transportIcons,
            settingsIcons = settingsIcons,
            onPreviousClick = onPreviousClick,
            onSeekToStartClick = onSeekToStartClick,
            onSeekBackClick = onSeekBackClick,
            onPlayPauseClick = onPlayPauseClick,
            onSeekForwardClick = onSeekForwardClick,
            onSeekToEndClick = onSeekToEndClick,
            onNextClick = onNextClick,
            onSeek = { position, force ->
                seekGestureState.seekTo(
                    positionMs = position,
                    force = force,
                    showFeedback = false
                )
            },
            onClearTransientFeedback = gestureFeedbackState::clear,
            modifier = controlsModifier,
            videoRotationDegrees = videoRotationDegrees,
            videoPixelRatio = videoPixelRatio,
            topActions = topActions,
            supplementalContent = supplementalContent
        )
    }
}
