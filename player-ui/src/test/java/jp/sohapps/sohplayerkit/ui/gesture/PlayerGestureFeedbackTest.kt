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
import org.junit.Test

class PlayerGestureFeedbackTest {
    @Test
    fun playbackSpeedMovesByOneStep() {
        assertEquals(1.25f, playerPlaybackSpeedAfterStep(1.0f, 1))
        assertEquals(0.75f, playerPlaybackSpeedAfterStep(1.0f, -1))
    }

    @Test
    fun playbackSpeedClampsAtEnds() {
        assertEquals(2.0f, playerPlaybackSpeedAfterStep(2.0f, 1))
        assertEquals(0.25f, playerPlaybackSpeedAfterStep(0.25f, -1))
    }

    @Test
    fun unknownPlaybackSpeedStartsFromNormalSpeed() {
        assertEquals(1.25f, playerPlaybackSpeedAfterStep(1.1f, 1))
    }

    @Test
    fun durationFormattingMatchesPlayerFeedback() {
        assertEquals("0:00", formatPlayerGestureDuration(0L))
        assertEquals("1:05", formatPlayerGestureDuration(65_000L))
        assertEquals("1:01:05", formatPlayerGestureDuration(3_665_000L))
    }
}
