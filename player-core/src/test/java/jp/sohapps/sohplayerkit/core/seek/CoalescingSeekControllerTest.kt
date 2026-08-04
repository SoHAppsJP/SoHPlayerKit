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
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoalescingSeekControllerTest {
    @Test
    fun rapidRequestsKeepOnlyLatestPendingPosition() = runTest {
        val calls = mutableListOf<Long>()
        val controller = CoalescingSeekController(
            scope = this,
            minIntervalMs = 260L,
            seekAction = calls::add
        )

        controller.seekTo(positionMs = 100L, force = false)
        controller.seekTo(positionMs = 200L, force = false)
        controller.seekTo(positionMs = 300L, force = false)

        assertEquals(listOf(100L), calls)

        advanceTimeBy(260L)
        runCurrent()

        assertEquals(listOf(100L, 300L), calls)
    }

    @Test
    fun nextRequestRunsImmediatelyAfterControllerBecomesIdle() = runTest {
        val calls = mutableListOf<Long>()
        val controller = CoalescingSeekController(
            scope = this,
            minIntervalMs = 260L,
            seekAction = calls::add
        )

        controller.seekTo(positionMs = 100L, force = false)
        advanceTimeBy(260L)
        runCurrent()

        controller.seekTo(positionMs = 200L, force = false)

        assertEquals(listOf(100L, 200L), calls)
    }

    @Test
    fun forceRunsImmediatelyAndDiscardsPreviousPendingPosition() = runTest {
        val calls = mutableListOf<Long>()
        val controller = CoalescingSeekController(
            scope = this,
            minIntervalMs = 260L,
            seekAction = calls::add
        )

        controller.seekTo(positionMs = 100L, force = false)
        controller.seekTo(positionMs = 200L, force = false)
        controller.seekTo(positionMs = 900L, force = true)

        assertEquals(listOf(100L, 900L), calls)

        advanceTimeBy(260L)
        runCurrent()

        assertEquals(listOf(100L, 900L), calls)
    }

    @Test
    fun requestAfterForceIsDeliveredAtNextInterval() = runTest {
        val calls = mutableListOf<Long>()
        val controller = CoalescingSeekController(
            scope = this,
            minIntervalMs = 260L,
            seekAction = calls::add
        )

        controller.seekTo(positionMs = 100L, force = true)
        controller.seekTo(positionMs = 200L, force = false)
        controller.seekTo(positionMs = 300L, force = false)

        assertEquals(listOf(100L), calls)

        advanceTimeBy(260L)
        runCurrent()

        assertEquals(listOf(100L, 300L), calls)
    }
}
