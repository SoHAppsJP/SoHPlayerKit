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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/** Engine-neutral playback values consumed by the shared player UI. */
data class PlayerPlaybackSnapshot(
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L
)

/**
 * Compose state holder for the basic playback snapshot shared by player engines.
 *
 * Engines remain responsible for deciding how and when these values are sampled. This class
 * only normalizes the values and gives callbacks, polling loops, seek previews, and controls a
 * single consistent source of truth.
 */
@Stable
class PlayerPlaybackState internal constructor(
    initialSnapshot: PlayerPlaybackSnapshot
) {
    var snapshot by mutableStateOf(normalizePlayerPlaybackSnapshot(initialSnapshot))
        private set

    val isPlaying: Boolean
        get() = snapshot.isPlaying

    val currentPositionMs: Long
        get() = snapshot.currentPositionMs

    val durationMs: Long
        get() = snapshot.durationMs

    fun update(
        isPlaying: Boolean,
        currentPositionMs: Long,
        durationMs: Long
    ) {
        snapshot = normalizePlayerPlaybackSnapshot(
            PlayerPlaybackSnapshot(
                isPlaying = isPlaying,
                currentPositionMs = currentPositionMs,
                durationMs = durationMs
            )
        )
    }

    fun updatePlaying(isPlaying: Boolean) {
        if (snapshot.isPlaying != isPlaying) {
            snapshot = snapshot.copy(isPlaying = isPlaying)
        }
    }

    fun updatePosition(currentPositionMs: Long) {
        val normalized = currentPositionMs.coerceAtLeast(0L)
        if (snapshot.currentPositionMs != normalized) {
            snapshot = snapshot.copy(currentPositionMs = normalized)
        }
    }

    fun updateDuration(durationMs: Long) {
        val normalized = durationMs.coerceAtLeast(0L)
        if (snapshot.durationMs != normalized) {
            snapshot = snapshot.copy(durationMs = normalized)
        }
    }

    /** Keeps the previous duration when the engine still reports an unknown/zero duration. */
    fun updateDurationIfKnown(durationMs: Long) {
        if (durationMs > 0L) {
            updateDuration(durationMs)
        }
    }
}

@Composable
fun rememberPlayerPlaybackState(
    key: Any? = Unit,
    initialIsPlaying: Boolean = false,
    initialPositionMs: Long = 0L,
    initialDurationMs: Long = 0L
): PlayerPlaybackState {
    return remember(key) {
        PlayerPlaybackState(
            PlayerPlaybackSnapshot(
                isPlaying = initialIsPlaying,
                currentPositionMs = initialPositionMs,
                durationMs = initialDurationMs
            )
        )
    }
}

internal fun normalizePlayerPlaybackSnapshot(
    snapshot: PlayerPlaybackSnapshot
): PlayerPlaybackSnapshot {
    return snapshot.copy(
        currentPositionMs = snapshot.currentPositionMs.coerceAtLeast(0L),
        durationMs = snapshot.durationMs.coerceAtLeast(0L)
    )
}
