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

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerPseudoFrameStepGateTest {
    @Test
    fun firstStepIsAccepted() {
        val gate = PlayerPseudoFrameStepGate()

        assertTrue(gate.tryBegin(stepCount = 1, direction = 1))
        assertTrue(gate.isAwaitingCompletion)
    }

    @Test
    fun additionalStepWhileAwaitingIsDroppedAndNotQueued() {
        val gate = PlayerPseudoFrameStepGate()

        assertTrue(gate.tryBegin(stepCount = 1, direction = 1))
        assertFalse(gate.tryBegin(stepCount = 2, direction = 1))

        gate.complete()

        // 描画待ち中に受け取った2は処理済み入力として記録されている。
        assertFalse(gate.tryBegin(stepCount = 2, direction = 1))
        assertTrue(gate.tryBegin(stepCount = 3, direction = 1))
    }

    @Test
    fun nextNewStepIsAcceptedAfterCompletion() {
        val gate = PlayerPseudoFrameStepGate()

        assertTrue(gate.tryBegin(stepCount = 1, direction = -1))
        gate.complete()

        assertTrue(gate.tryBegin(stepCount = 2, direction = -1))
    }

    @Test
    fun newSequenceStartsFromStepOne() {
        val gate = PlayerPseudoFrameStepGate()

        assertTrue(gate.tryBegin(stepCount = 1, direction = 1))
        assertTrue(gate.tryBegin(stepCount = 1, direction = 1))
    }

    @Test
    fun directionChangeStartsNewSequence() {
        val gate = PlayerPseudoFrameStepGate()

        assertTrue(gate.tryBegin(stepCount = 1, direction = 1))
        assertTrue(gate.tryBegin(stepCount = 2, direction = -1))
    }

    @Test
    fun zeroDirectionIsRejected() {
        val gate = PlayerPseudoFrameStepGate()

        assertFalse(gate.tryBegin(stepCount = 1, direction = 0))
        assertFalse(gate.isAwaitingCompletion)
    }

    @Test
    fun resetClearsAwaitingState() {
        val gate = PlayerPseudoFrameStepGate()

        assertTrue(gate.tryBegin(stepCount = 1, direction = 1))
        gate.reset()

        assertFalse(gate.isAwaitingCompletion)
        assertTrue(gate.tryBegin(stepCount = 1, direction = 1))
    }
}
