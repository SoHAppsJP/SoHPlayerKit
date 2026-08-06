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

package jp.sohapps.sohplayerkit.core.seek

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerSeekGestureStateTest {
    @Test
    fun seekToClampsPositionAndCanSuppressFeedback() = runTest {
        var currentPosition = 500L
        var duration = 1_000L
        val previews = mutableListOf<Long>()
        val feedback = mutableListOf<Pair<Long, Long>>()
        val seeks = mutableListOf<Long>()
        val state = PlayerSeekGestureState(
            scope = this,
            currentPositionMs = { currentPosition },
            durationMs = { duration },
            playbackSpeed = { 1.0f },
            onPreviewPositionChanged = {
                currentPosition = it
                previews += it
            },
            onSeekFeedback = { position, length -> feedback += position to length },
            minIntervalMs = 260L,
            seekAction = seeks::add
        )

        assertEquals(1_000L, state.seekTo(1_500L, force = true, showFeedback = false))
        assertEquals(listOf(1_000L), previews)
        assertEquals(listOf(1_000L), seeks)
        assertTrue(feedback.isEmpty())

        duration = 0L
        assertEquals(0L, state.seekTo(-200L, force = true))
        assertEquals(0L to 0L, feedback.single())
    }

    @Test
    fun dragAccumulatesFromPreviousPreviewAndCommitForcesLatestPosition() = runTest {
        var currentPosition = 1_000L
        val feedback = mutableListOf<Pair<Long, Long>>()
        val seeks = mutableListOf<Long>()
        val state = PlayerSeekGestureState(
            scope = this,
            currentPositionMs = { currentPosition },
            durationMs = { 10_000L },
            playbackSpeed = { 1.0f },
            onPreviewPositionChanged = { currentPosition = it },
            onSeekFeedback = { position, length -> feedback += position to length },
            minIntervalMs = 260L,
            seekAction = seeks::add
        )

        assertEquals(1_080L, state.dragBy(1.0f))
        assertEquals(1_240L, state.dragBy(2.0f))
        assertTrue(state.hasDragPreview())
        assertEquals(listOf(1_080L), seeks)

        assertEquals(1_240L, state.commitDrag())
        assertFalse(state.hasDragPreview())
        assertEquals(listOf(1_080L, 1_240L), seeks)
        assertEquals(
            listOf(
                1_080L to 10_000L,
                1_240L to 10_000L,
                1_240L to 10_000L
            ),
            feedback
        )

        advanceTimeBy(260L)
        runCurrent()
        assertEquals(listOf(1_080L, 1_240L), seeks)
    }

    @Test
    fun cancelDragDiscardsPreviewWithoutFinalSeek() = runTest {
        var currentPosition = 1_000L
        val seeks = mutableListOf<Long>()
        val state = PlayerSeekGestureState(
            scope = this,
            currentPositionMs = { currentPosition },
            durationMs = { 10_000L },
            playbackSpeed = { 1.0f },
            onPreviewPositionChanged = { currentPosition = it },
            onSeekFeedback = { _, _ -> },
            seekAction = seeks::add
        )

        state.dragBy(1.0f)
        state.cancelDrag()

        assertFalse(state.hasDragPreview())
        assertNull(state.commitDrag())
        assertEquals(listOf(1_080L), seeks)
    }
}
