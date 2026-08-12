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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import jp.sohapps.sohplayerkit.core.seek.PlayerSeekGestureState
import jp.sohapps.sohplayerkit.ui.controls.PlayerControlsVisibilityState
import jp.sohapps.sohplayerkit.ui.gesture.PlayerGestureActionController
import jp.sohapps.sohplayerkit.ui.gesture.PlayerGestureFeedbackOverlay
import jp.sohapps.sohplayerkit.ui.gesture.PlayerGestureFeedbackState
import jp.sohapps.sohplayerkit.ui.gesture.PlayerGestureOverlay
import jp.sohapps.sohplayerkit.ui.gesture.PlayerZoomState

/**
 * Shared interaction/status overlay stack for a player surface.
 *
 * The host keeps playback-engine operations outside the library by supplying gesture callbacks.
 * [controlsContent] is emitted last so the caller can keep its existing control-panel composition
 * and BoxScope positioning while the common gesture, feedback, and status layer ordering lives in
 * one place.
 */
@Composable
fun BoxScope.PlayerOverlayHost(
    isZoomed: Boolean,
    gestureFeedbackState: PlayerGestureFeedbackState,
    statusState: PlayerStatusState,
    onTap: () -> Unit,
    onDoubleTapLeft: (Int) -> Unit,
    onDoubleTapCenter: () -> Unit,
    onDoubleTapRight: (Int) -> Unit,
    onHorizontalDrag: (Float) -> Unit,
    onVerticalDragLeft: (Int) -> Unit,
    onVerticalDragCenter: (Int) -> Unit,
    onVerticalDragRight: (Int) -> Unit,
    onPinchTransform: (Float, Offset) -> Unit,
    onGestureEnd: () -> Unit,
    gestureEnabled: Boolean = true,
    controlsContent: @Composable BoxScope.() -> Unit = {}
) {
    if (gestureEnabled) {
        PlayerGestureOverlay(
            isZoomed = isZoomed,
            onTap = onTap,
            onDoubleTapLeft = onDoubleTapLeft,
            onDoubleTapCenter = onDoubleTapCenter,
            onDoubleTapRight = onDoubleTapRight,
            onHorizontalDrag = onHorizontalDrag,
            onVerticalDragLeft = onVerticalDragLeft,
            onVerticalDragCenter = onVerticalDragCenter,
            onVerticalDragRight = onVerticalDragRight,
            onPinchTransform = onPinchTransform,
            onGestureEnd = onGestureEnd
        )
    }

    PlayerGestureFeedbackOverlay(
        state = gestureFeedbackState,
        modifier = Modifier.fillMaxSize()
    )

    PlayerStatusOverlay(
        state = statusState,
        modifier = Modifier.fillMaxSize()
    )

    controlsContent()
}

/**
 * Binds the shared player interaction state holders to [PlayerOverlayHost].
 *
 * Playback-engine-specific double-tap actions remain callbacks, while the common tap, drag,
 * brightness, speed, volume, zoom, seek-commit, feedback-clear, and overlay ordering behavior is
 * kept in one place for ExoPlayer and LibVLC hosts.
 */
@Composable
fun BoxScope.PlayerInteractionOverlayHost(
    controlsState: PlayerControlsVisibilityState,
    zoomState: PlayerZoomState,
    gestureFeedbackState: PlayerGestureFeedbackState,
    gestureActionController: PlayerGestureActionController,
    seekGestureState: PlayerSeekGestureState,
    statusState: PlayerStatusState,
    onDoubleTapLeft: (Int) -> Unit,
    onDoubleTapCenter: () -> Unit,
    onDoubleTapRight: (Int) -> Unit,
    gestureEnabled: Boolean = true,
    controlsContent: @Composable BoxScope.() -> Unit = {}
) {
    PlayerOverlayHost(
        isZoomed = zoomState.isZoomed,
        gestureFeedbackState = gestureFeedbackState,
        statusState = statusState,
        onTap = controlsState::toggle,
        onDoubleTapLeft = onDoubleTapLeft,
        onDoubleTapCenter = onDoubleTapCenter,
        onDoubleTapRight = onDoubleTapRight,
        onHorizontalDrag = { deltaX -> seekGestureState.dragBy(deltaX) },
        onVerticalDragLeft = gestureActionController::changeBrightness,
        onVerticalDragCenter = gestureActionController::changeSpeed,
        onVerticalDragRight = gestureActionController::changeVolume,
        onPinchTransform = { zoom, pan ->
            zoomState.transform(
                zoomChange = zoom,
                panX = pan.x,
                panY = pan.y
            )?.let(gestureFeedbackState::showZoom)
        },
        onGestureEnd = {
            seekGestureState.commitDrag()
            gestureFeedbackState.clear()
        },
        gestureEnabled = gestureEnabled,
        controlsContent = controlsContent
    )
}
