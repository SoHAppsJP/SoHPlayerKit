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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

const val PLAYER_ZOOM_MIN_SCALE = 1.0f
const val PLAYER_ZOOM_MAX_SCALE = 8.0f
const val PLAYER_ZOOM_SNAP_TO_MIN_THRESHOLD = 1.01f
const val PLAYER_ZOOM_MAX_PAN_PX = 2500.0f

/**
 * Shared pinch-zoom and pan state for player surfaces.
 *
 * The state preserves the behavior used by the main player: scale is limited to 1x-8x,
 * values close to 1x snap back to exactly 1x, and pan is reset whenever zoom returns to 1x.
 */
@Stable
class PlayerZoomState internal constructor(
    private val minScale: Float = PLAYER_ZOOM_MIN_SCALE,
    private val maxScale: Float = PLAYER_ZOOM_MAX_SCALE,
    private val snapToMinThreshold: Float = PLAYER_ZOOM_SNAP_TO_MIN_THRESHOLD,
    private val maxPanPx: Float = PLAYER_ZOOM_MAX_PAN_PX
) {
    var scale by mutableFloatStateOf(minScale)
        private set

    var offsetX by mutableFloatStateOf(0.0f)
        private set

    var offsetY by mutableFloatStateOf(0.0f)
        private set

    val isZoomed: Boolean
        get() = scale > snapToMinThreshold

    /**
     * Applies one transform update.
     *
     * Returns the resulting scale when the pinch changed scale, allowing the caller to show
     * zoom feedback. A pan-only update returns null.
     */
    fun transform(
        zoomChange: Float,
        panX: Float,
        panY: Float
    ): Float? {
        val rawNextScale = (scale * zoomChange).coerceIn(minScale, maxScale)
        val nextScale = if (rawNextScale <= snapToMinThreshold) minScale else rawNextScale
        scale = nextScale

        if (nextScale <= minScale) {
            offsetX = 0.0f
            offsetY = 0.0f
        } else {
            offsetX = (offsetX + panX).coerceIn(-maxPanPx, maxPanPx)
            offsetY = (offsetY + panY).coerceIn(-maxPanPx, maxPanPx)
        }

        return nextScale.takeIf { zoomChange != 1.0f }
    }

    fun reset() {
        scale = minScale
        offsetX = 0.0f
        offsetY = 0.0f
    }
}

@Composable
fun rememberPlayerZoomState(): PlayerZoomState {
    return remember { PlayerZoomState() }
}

/** Applies the current shared zoom and pan state to a player rendering surface. */
fun Modifier.playerZoomGraphicsLayer(state: PlayerZoomState): Modifier {
    return graphicsLayer(
        scaleX = state.scale,
        scaleY = state.scale,
        translationX = state.offsetX,
        translationY = state.offsetY
    )
}
