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

package jp.sohapps.sohplayerkit.ui.gesture

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * プレイヤー共通のタップ、ドラッグ、ピンチジェスチャーを検出する透明オーバーレイ。
 *
 * 再生エンジンには依存せず、検出した操作をコールバックとして通知する。
 * 左右のダブルタップ後に続くタップは、最初に決めた方向の連続操作として扱う。
 */
@Composable
fun PlayerGestureOverlay(
    isZoomed: Boolean,
    onTap: () -> Unit,
    onDoubleTapLeft: (Int) -> Unit,
    onDoubleTapCenter: () -> Unit,
    onDoubleTapRight: (Int) -> Unit,
    onHorizontalDrag: (Float) -> Unit,
    onVerticalDragLeft: (Int) -> Unit,
    onVerticalDragCenter: (Int) -> Unit,
    onVerticalDragRight: (Int) -> Unit,
    onPinchTransform: (Float, Offset) -> Unit,
    onGestureEnd: () -> Unit
) {
    val isZoomedState = rememberUpdatedState(isZoomed)
    val onTapState = rememberUpdatedState(onTap)
    val onDoubleTapLeftState = rememberUpdatedState(onDoubleTapLeft)
    val onDoubleTapCenterState = rememberUpdatedState(onDoubleTapCenter)
    val onDoubleTapRightState = rememberUpdatedState(onDoubleTapRight)
    val onHorizontalDragState = rememberUpdatedState(onHorizontalDrag)
    val onVerticalDragLeftState = rememberUpdatedState(onVerticalDragLeft)
    val onVerticalDragCenterState = rememberUpdatedState(onVerticalDragCenter)
    val onVerticalDragRightState = rememberUpdatedState(onVerticalDragRight)
    val onPinchTransformState = rememberUpdatedState(onPinchTransform)
    val onGestureEndState = rememberUpdatedState(onGestureEnd)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                coroutineScope {
                    // Keep one tap sequence open while taps continue within the timeout.
                    // Only the first pair needs to be spatially close; later taps keep the first direction.
                    var tapCount = 0
                    var firstTapPosition = Offset.Zero
                    var tapSection = PlayerTapSection.CENTER
                    var tapSequenceJob: Job? = null

                    fun resetTapSequence() {
                        tapSequenceJob?.cancel()
                        tapSequenceJob = null
                        tapCount = 0
                    }

                    fun scheduleTapSequenceEnd() {
                        tapSequenceJob?.cancel()
                        tapSequenceJob = launch {
                            delay(PLAYER_MULTI_TAP_CONTINUATION_TIMEOUT_MS)
                            if (tapCount == 1) {
                                onTapState.value()
                            }
                            tapCount = 0
                            tapSequenceJob = null
                        }
                    }

                    fun beginTapSequence(position: Offset) {
                        val sectionWidth = size.width / 3f
                        firstTapPosition = position
                        tapSection = when {
                            position.x < sectionWidth -> PlayerTapSection.LEFT
                            position.x > sectionWidth * 2f -> PlayerTapSection.RIGHT
                            else -> PlayerTapSection.CENTER
                        }
                        tapCount = 1
                        scheduleTapSequenceEnd()
                    }

                    fun registerTap(position: Offset) {
                        if (tapCount == 0) {
                            beginTapSequence(position)
                            return
                        }

                        if (tapCount == 1) {
                            val deltaX = position.x - firstTapPosition.x
                            val deltaY = position.y - firstTapPosition.y
                            val distance = sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()
                            val initialDoubleTapSlop = viewConfiguration.touchSlop * 4f
                            if (distance > initialDoubleTapSlop) {
                                tapSequenceJob?.cancel()
                                onTapState.value()
                                beginTapSequence(position)
                                return
                            }
                        }

                        tapCount += 1
                        if (tapCount >= 2) {
                            val seekStepCount = tapCount - 1
                            when (tapSection) {
                                PlayerTapSection.LEFT -> onDoubleTapLeftState.value(seekStepCount)
                                PlayerTapSection.RIGHT -> onDoubleTapRightState.value(seekStepCount)
                                PlayerTapSection.CENTER -> {
                                    // Center double-tap toggles once; all later taps are consumed silently.
                                    if (tapCount == 2) {
                                        onDoubleTapCenterState.value()
                                    }
                                }
                            }
                        }
                        scheduleTapSequenceEnd()
                    }

                    awaitEachGesture {
                        val firstDown = awaitFirstDown(requireUnconsumed = false)
                        if (tapCount > 0) {
                            tapSequenceJob?.cancel()
                        }
                        val tapPosition = firstDown.position

                        var startX = 0f
                        var totalX = 0f
                        var totalY = 0f
                        var lockedDirection = GestureDirection.NONE
                        var verticalAccumulator = 0f
                        var gestureStarted = false
                        var tapCandidate = true
                        var multiTouchActive = false
                        var initialPinchDistance = 0.0f
                        var lastAppliedPinchScale = 1.0f
                        var pinchZoomArmed = false

                        while (true) {
                            val event = awaitPointerEvent()
                            val pressedCount = event.changes.count { it.pressed }
                            val pan = event.calculatePan()
                            val centroid = event.calculateCentroid(useCurrent = true)

                            if (pressedCount == 0) {
                                break
                            }

                            if (isZoomedState.value) {
                                totalX += pan.x
                                totalY += pan.y

                                val zoomStartThreshold = 0.025f
                                val zoomDamping = 0.65f
                                val singleFingerPanStartThreshold = 8.0f
                                val panActive = abs(pan.x) > 0.4f || abs(pan.y) > 0.4f
                                val currentDistance = calculatePressedPointerDistance(event.changes)
                                var effectiveZoom = 1.0f

                                if (pressedCount >= 2) {
                                    tapCandidate = false
                                    if (!multiTouchActive) {
                                        multiTouchActive = true
                                        initialPinchDistance = currentDistance
                                        lastAppliedPinchScale = 1.0f
                                        pinchZoomArmed = false
                                    }

                                    if (initialPinchDistance > 0.0f && currentDistance > 0.0f) {
                                        val scaleFromStart = currentDistance / initialPinchDistance
                                        if (!pinchZoomArmed && abs(scaleFromStart - 1.0f) >= zoomStartThreshold) {
                                            pinchZoomArmed = true
                                            effectiveZoom = 1.0f + ((scaleFromStart - 1.0f) * zoomDamping)
                                            lastAppliedPinchScale = scaleFromStart
                                        } else if (pinchZoomArmed && lastAppliedPinchScale > 0.0f) {
                                            val rawZoom = scaleFromStart / lastAppliedPinchScale
                                            effectiveZoom = 1.0f + ((rawZoom - 1.0f) * zoomDamping)
                                            lastAppliedPinchScale = scaleFromStart
                                        }
                                    }

                                    if (panActive || effectiveZoom != 1.0f) {
                                        onPinchTransformState.value(effectiveZoom, pan)
                                        gestureStarted = true
                                        tapCandidate = false
                                        event.changes.forEach { it.consume() }
                                    }
                                } else if (pressedCount == 1) {
                                    val panDistance = sqrt((totalX * totalX + totalY * totalY).toDouble()).toFloat()
                                    if (gestureStarted || panDistance >= singleFingerPanStartThreshold) {
                                        if (panActive) {
                                            onPinchTransformState.value(1.0f, pan)
                                        }
                                        gestureStarted = true
                                        tapCandidate = false
                                        event.changes.forEach { it.consume() }
                                    }
                                }

                                if (!event.changes.any { it.pressed }) {
                                    break
                                }
                                continue
                            }

                            if (pressedCount >= 2 || multiTouchActive) {
                                tapCandidate = false
                                val zoomStartThreshold = 0.04f
                                val zoomFrameDeadZone = 0.0f
                                val zoomDamping = 0.65f
                                val minimumStablePinchDistance = 64.0f
                                val panActive = abs(pan.x) > 0.4f || abs(pan.y) > 0.4f
                                val currentDistance = calculatePressedPointerDistance(event.changes)
                                var effectiveZoom = 1.0f

                                if (!multiTouchActive) {
                                    multiTouchActive = true
                                    initialPinchDistance = currentDistance
                                    lastAppliedPinchScale = 1.0f
                                    pinchZoomArmed = false
                                    totalX = 0f
                                    totalY = 0f
                                    verticalAccumulator = 0f
                                    lockedDirection = GestureDirection.NONE
                                }

                                if (pressedCount >= 2 && currentDistance > 0.0f) {
                                    if (initialPinchDistance < minimumStablePinchDistance && currentDistance >= minimumStablePinchDistance * 1.5f && !pinchZoomArmed) {
                                        initialPinchDistance = currentDistance
                                        lastAppliedPinchScale = 1.0f
                                    }

                                    if (initialPinchDistance >= minimumStablePinchDistance) {
                                        val scaleFromStart = currentDistance / initialPinchDistance
                                        if (!pinchZoomArmed && abs(scaleFromStart - 1.0f) >= zoomStartThreshold) {
                                            pinchZoomArmed = true
                                            effectiveZoom = 1.0f + ((scaleFromStart - 1.0f) * zoomDamping)
                                            lastAppliedPinchScale = scaleFromStart
                                        } else if (pinchZoomArmed && lastAppliedPinchScale > 0.0f) {
                                            val rawZoom = scaleFromStart / lastAppliedPinchScale
                                            if (abs(rawZoom - 1.0f) >= zoomFrameDeadZone) {
                                                effectiveZoom = 1.0f + ((rawZoom - 1.0f) * zoomDamping)
                                                lastAppliedPinchScale = scaleFromStart
                                            }
                                        }
                                    }
                                }

                                if (pressedCount >= 2 && (effectiveZoom != 1.0f || panActive)) {
                                    onPinchTransformState.value(effectiveZoom, pan)
                                    gestureStarted = true
                                    tapCandidate = false
                                }

                                event.changes.forEach { it.consume() }

                                if (!event.changes.any { it.pressed }) {
                                    break
                                }
                                continue
                            }

                            totalX += pan.x
                            totalY += pan.y

                            if (lockedDirection == GestureDirection.NONE) {
                                startX = centroid.x

                                if (abs(totalX) < 28f && abs(totalY) < 28f) {
                                    continue
                                }

                                lockedDirection = if (abs(totalX) >= abs(totalY)) {
                                    GestureDirection.HORIZONTAL
                                } else {
                                    GestureDirection.VERTICAL
                                }
                                gestureStarted = true
                                tapCandidate = false
                            }

                            when (lockedDirection) {
                                GestureDirection.HORIZONTAL -> {
                                    onHorizontalDragState.value(pan.x)
                                    event.changes.forEach { it.consume() }
                                }

                                GestureDirection.VERTICAL -> {
                                    verticalAccumulator += pan.y
                                    val sectionWidth = size.width / 3f
                                    val isCenterSection = startX >= sectionWidth && startX <= sectionWidth * 2f
                                    val verticalStepThreshold = if (isCenterSection) 95f else 55f
                                    if (abs(verticalAccumulator) >= verticalStepThreshold) {
                                        val step = if (verticalAccumulator < 0f) 1 else -1
                                        when {
                                            startX < sectionWidth -> onVerticalDragLeftState.value(step)
                                            startX > sectionWidth * 2f -> onVerticalDragRightState.value(step)
                                            else -> onVerticalDragCenterState.value(step)
                                        }
                                        verticalAccumulator = 0f
                                    }
                                    event.changes.forEach { it.consume() }
                                }

                                GestureDirection.NONE -> {
                                }
                            }
                            if (!event.changes.any { it.pressed }) {
                                break
                            }
                        }

                        if (gestureStarted) {
                            resetTapSequence()
                            onGestureEndState.value()
                        } else if (tapCandidate && !multiTouchActive) {
                            registerTap(tapPosition)
                        } else {
                            resetTapSequence()
                        }
                    }
                }
            }
    )
}


private fun calculatePressedPointerDistance(changes: List<androidx.compose.ui.input.pointer.PointerInputChange>): Float {
    val pressed = changes.filter { it.pressed }
    if (pressed.size < 2) {
        return 0.0f
    }
    val first = pressed[0].position
    val second = pressed[1].position
    val dx = first.x - second.x
    val dy = first.y - second.y
    return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
}

private const val PLAYER_MULTI_TAP_CONTINUATION_TIMEOUT_MS = 360L

private enum class PlayerTapSection { LEFT, CENTER, RIGHT }
private enum class GestureDirection { NONE, HORIZONTAL, VERTICAL }
