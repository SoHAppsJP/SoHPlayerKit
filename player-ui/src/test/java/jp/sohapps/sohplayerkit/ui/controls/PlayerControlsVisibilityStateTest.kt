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

package jp.sohapps.sohplayerkit.ui.controls

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerControlsVisibilityStateTest {
    @Test
    fun showMakesControlsVisibleAndRefreshesInteractionVersion() {
        val state = PlayerControlsVisibilityState()

        state.show()
        state.show()

        assertTrue(state.visible)
        assertEquals(2, state.interactionVersion)
    }

    @Test
    fun hideClosesExpandedMenu() {
        val state = PlayerControlsVisibilityState()
        state.toggleMenu(PlayerMenu.SPEED)

        state.hide()

        assertFalse(state.visible)
        assertEquals(PlayerMenu.NONE, state.menu)
    }

    @Test
    fun toggleShowsThenHidesControls() {
        val state = PlayerControlsVisibilityState()

        state.toggle()
        assertTrue(state.visible)

        state.toggle()
        assertFalse(state.visible)
        assertEquals(PlayerMenu.NONE, state.menu)
    }

    @Test
    fun toggleMenuShowsControlsAndTogglesSameMenuClosed() {
        val state = PlayerControlsVisibilityState()

        state.toggleMenu(PlayerMenu.ASPECT)
        assertTrue(state.visible)
        assertEquals(PlayerMenu.ASPECT, state.menu)

        state.toggleMenu(PlayerMenu.ASPECT)
        assertTrue(state.visible)
        assertEquals(PlayerMenu.NONE, state.menu)
    }

    @Test
    fun autoHideRequiresVisiblePlayingControlsWithoutOpenMenu() {
        assertTrue(
            shouldAutoHidePlayerControls(
                visible = true,
                isPlaying = true,
                menu = PlayerMenu.NONE
            )
        )
        assertFalse(
            shouldAutoHidePlayerControls(
                visible = false,
                isPlaying = true,
                menu = PlayerMenu.NONE
            )
        )
        assertFalse(
            shouldAutoHidePlayerControls(
                visible = true,
                isPlaying = false,
                menu = PlayerMenu.NONE
            )
        )
        assertFalse(
            shouldAutoHidePlayerControls(
                visible = true,
                isPlaying = true,
                menu = PlayerMenu.INFO
            )
        )
    }
}
