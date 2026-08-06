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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerZoomStateTest {
    @Test
    fun defaultsToUnzoomedOrigin() {
        val state = PlayerZoomState()

        assertEquals(1.0f, state.scale)
        assertEquals(0.0f, state.offsetX)
        assertEquals(0.0f, state.offsetY)
        assertFalse(state.isZoomed)
    }

    @Test
    fun appliesZoomAndPan() {
        val state = PlayerZoomState()

        val feedbackScale = state.transform(
            zoomChange = 2.0f,
            panX = 120.0f,
            panY = -80.0f
        )

        assertEquals(2.0f, feedbackScale)
        assertEquals(2.0f, state.scale)
        assertEquals(120.0f, state.offsetX)
        assertEquals(-80.0f, state.offsetY)
        assertTrue(state.isZoomed)
    }

    @Test
    fun snapsToOneAndClearsPan() {
        val state = PlayerZoomState()
        state.transform(zoomChange = 2.0f, panX = 100.0f, panY = 200.0f)

        state.transform(zoomChange = 0.5f, panX = 30.0f, panY = 40.0f)

        assertEquals(1.0f, state.scale)
        assertEquals(0.0f, state.offsetX)
        assertEquals(0.0f, state.offsetY)
        assertFalse(state.isZoomed)
    }

    @Test
    fun clampsScaleAndPan() {
        val state = PlayerZoomState()

        state.transform(
            zoomChange = 100.0f,
            panX = 10_000.0f,
            panY = -10_000.0f
        )

        assertEquals(8.0f, state.scale)
        assertEquals(2500.0f, state.offsetX)
        assertEquals(-2500.0f, state.offsetY)
    }

    @Test
    fun panOnlyUpdateDoesNotRequestZoomFeedback() {
        val state = PlayerZoomState()
        state.transform(zoomChange = 2.0f, panX = 0.0f, panY = 0.0f)

        val feedbackScale = state.transform(
            zoomChange = 1.0f,
            panX = 25.0f,
            panY = 30.0f
        )

        assertNull(feedbackScale)
        assertEquals(25.0f, state.offsetX)
        assertEquals(30.0f, state.offsetY)
    }

    @Test
    fun resetReturnsToOrigin() {
        val state = PlayerZoomState()
        state.transform(zoomChange = 3.0f, panX = 400.0f, panY = -500.0f)

        state.reset()

        assertEquals(1.0f, state.scale)
        assertEquals(0.0f, state.offsetX)
        assertEquals(0.0f, state.offsetY)
        assertFalse(state.isZoomed)
    }
}
