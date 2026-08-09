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
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerGestureActionControllerTest {
    @Test
    fun brightnessUsesReturnedPercentForFeedback() {
        val feedback = PlayerGestureFeedbackState()
        var receivedStep = 0
        val controller = PlayerGestureActionController(
            changeBrightnessBySteps = { step ->
                receivedStep = step
                65
            },
            changeVolumeBySteps = { 0 },
            currentPlaybackSpeed = { 1.0f },
            onPlaybackSpeedChanged = {},
            feedbackState = feedback
        )

        controller.changeBrightness(1)

        assertEquals(1, receivedStep)
        assertEquals("明るさ 65%", feedback.text)
    }

    @Test
    fun unavailableBrightnessDoesNotReplaceFeedback() {
        val feedback = PlayerGestureFeedbackState().apply {
            showMessage("existing")
        }
        val controller = PlayerGestureActionController(
            changeBrightnessBySteps = null,
            changeVolumeBySteps = { 0 },
            currentPlaybackSpeed = { 1.0f },
            onPlaybackSpeedChanged = {},
            feedbackState = feedback
        )

        controller.changeBrightness(1)

        assertEquals("existing", feedback.text)
    }

    @Test
    fun volumeUsesReturnedPercentForFeedback() {
        val feedback = PlayerGestureFeedbackState()
        var receivedStep = 0
        val controller = PlayerGestureActionController(
            changeBrightnessBySteps = null,
            changeVolumeBySteps = { step ->
                receivedStep = step
                40
            },
            currentPlaybackSpeed = { 1.0f },
            onPlaybackSpeedChanged = {},
            feedbackState = feedback
        )

        controller.changeVolume(-1)

        assertEquals(-1, receivedStep)
        assertEquals("音量 40%", feedback.text)
    }

    @Test
    fun speedStepAppliesChangedSpeedAndShowsFeedback() {
        val feedback = PlayerGestureFeedbackState()
        var currentSpeed = 1.0f
        var callbackCalled = false
        val controller = PlayerGestureActionController(
            changeBrightnessBySteps = null,
            changeVolumeBySteps = { 0 },
            currentPlaybackSpeed = { currentSpeed },
            onPlaybackSpeedChanged = { speed ->
                callbackCalled = true
                currentSpeed = speed
            },
            feedbackState = feedback
        )

        controller.changeSpeed(1)

        assertTrue(callbackCalled)
        assertEquals(1.25f, currentSpeed)
        assertEquals("速度 1.25x", feedback.text)
    }

    @Test
    fun speedBoundaryDoesNotReapplyButStillShowsFeedback() {
        val feedback = PlayerGestureFeedbackState()
        var callbackCalled = false
        val controller = PlayerGestureActionController(
            changeBrightnessBySteps = null,
            changeVolumeBySteps = { 0 },
            currentPlaybackSpeed = { 2.0f },
            onPlaybackSpeedChanged = { callbackCalled = true },
            feedbackState = feedback
        )

        controller.changeSpeed(1)

        assertFalse(callbackCalled)
        assertEquals("速度 2.0x", feedback.text)
    }
}
