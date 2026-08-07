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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerPlaybackStateTest {
    @Test
    fun negativePositionAndDurationAreNormalized() {
        val normalized = normalizePlayerPlaybackSnapshot(
            PlayerPlaybackSnapshot(
                isPlaying = true,
                currentPositionMs = -100L,
                durationMs = -200L
            )
        )

        assertTrue(normalized.isPlaying)
        assertEquals(0L, normalized.currentPositionMs)
        assertEquals(0L, normalized.durationMs)
    }

    @Test
    fun updateReplacesWholeSnapshot() {
        val state = PlayerPlaybackState(PlayerPlaybackSnapshot())

        state.update(
            isPlaying = true,
            currentPositionMs = 12_345L,
            durationMs = 60_000L
        )

        assertTrue(state.isPlaying)
        assertEquals(12_345L, state.currentPositionMs)
        assertEquals(60_000L, state.durationMs)
    }

    @Test
    fun updateDurationIfKnownKeepsPreviousValueForUnknownDuration() {
        val state = PlayerPlaybackState(
            PlayerPlaybackSnapshot(durationMs = 90_000L)
        )

        state.updateDurationIfKnown(0L)
        assertEquals(90_000L, state.durationMs)

        state.updateDurationIfKnown(120_000L)
        assertEquals(120_000L, state.durationMs)
    }

    @Test
    fun individualUpdatesPreserveOtherValues() {
        val state = PlayerPlaybackState(
            PlayerPlaybackSnapshot(
                isPlaying = true,
                currentPositionMs = 1_000L,
                durationMs = 5_000L
            )
        )

        state.updatePosition(2_000L)
        state.updatePlaying(false)

        assertFalse(state.isPlaying)
        assertEquals(2_000L, state.currentPositionMs)
        assertEquals(5_000L, state.durationMs)
    }
}
