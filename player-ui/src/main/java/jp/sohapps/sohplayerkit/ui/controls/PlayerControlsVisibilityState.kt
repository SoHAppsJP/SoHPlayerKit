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

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

/**
 * Shared state for showing, hiding, and interacting with the player control panel.
 *
 * The host remains responsible for engine operations and for clearing any application-specific
 * overlays when controls are hidden. Each call to [show] refreshes [interactionVersion], allowing
 * [PlayerControlsVisibilityEffects] to restart the auto-hide timer even when already visible.
 */
@Stable
class PlayerControlsVisibilityState(
    initiallyVisible: Boolean = false,
    initialMenu: PlayerMenu = PlayerMenu.NONE
) {
    var visible: Boolean by mutableStateOf(initiallyVisible)
        private set

    var menu: PlayerMenu by mutableStateOf(initialMenu)
        private set

    var interactionVersion: Int by mutableIntStateOf(0)
        private set

    /** Shows the controls and refreshes the auto-hide timer. */
    fun show() {
        visible = true
        interactionVersion += 1
    }

    /** Hides the controls and closes any expanded settings menu. */
    fun hide() {
        visible = false
        menu = PlayerMenu.NONE
    }

    /** Toggles panel visibility. Hiding also closes any expanded settings menu. */
    fun toggle() {
        if (visible) {
            hide()
        } else {
            show()
        }
    }

    /** Shows the panel and toggles the requested settings menu. */
    fun toggleMenu(nextMenu: PlayerMenu) {
        show()
        menu = if (menu == nextMenu) PlayerMenu.NONE else nextMenu
    }
}

/** Remembers a [PlayerControlsVisibilityState] for a player surface. */
@Composable
fun rememberPlayerControlsVisibilityState(
    initiallyVisible: Boolean = false,
    initialMenu: PlayerMenu = PlayerMenu.NONE
): PlayerControlsVisibilityState {
    return remember {
        PlayerControlsVisibilityState(
            initiallyVisible = initiallyVisible,
            initialMenu = initialMenu
        )
    }
}

/**
 * Installs shared Back handling and auto-hide behavior for player controls.
 *
 * [onBackHide] can clear host-specific transient UI before or after calling [hide]. Auto-hide
 * intentionally performs only the shared state transition, matching the previous player behavior.
 */
@Composable
fun PlayerControlsVisibilityEffects(
    state: PlayerControlsVisibilityState,
    isPlaying: Boolean,
    autoHideMs: Long,
    onBackHide: () -> Unit = state::hide
) {
    BackHandler(enabled = state.visible) {
        onBackHide()
    }

    LaunchedEffect(
        state.visible,
        state.interactionVersion,
        isPlaying,
        state.menu,
        autoHideMs
    ) {
        if (shouldAutoHidePlayerControls(
                visible = state.visible,
                isPlaying = isPlaying,
                menu = state.menu
            )
        ) {
            delay(autoHideMs.coerceAtLeast(0L))
            state.hide()
        }
    }
}

internal fun shouldAutoHidePlayerControls(
    visible: Boolean,
    isPlaying: Boolean,
    menu: PlayerMenu
): Boolean {
    return visible && isPlaying && menu == PlayerMenu.NONE
}
