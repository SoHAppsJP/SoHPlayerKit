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

import android.view.Window
import androidx.compose.runtime.Composable
import jp.sohapps.sohplayerkit.ui.controls.PlayerControlsVisibilityEffects
import jp.sohapps.sohplayerkit.ui.system.PlayerKeepScreenOnEffect

/**
 * Installs the common lifecycle effects associated with a [PlayerUiState].
 *
 * The host still supplies the Android [window] and its preferred controls auto-hide duration,
 * while the shared UI state determines playback activity and the Back action used to hide
 * controls and clear transient gesture feedback.
 */
@Composable
fun PlayerUiEffects(
    state: PlayerUiState,
    window: Window?,
    controlsAutoHideMs: Long
) {
    PlayerControlsVisibilityEffects(
        state = state.controlsState,
        isPlaying = state.playbackState.isPlaying,
        autoHideMs = controlsAutoHideMs,
        onBackHide = state::hideControlsAndFeedback
    )

    PlayerKeepScreenOnEffect(
        window = window,
        isPlaying = state.playbackState.isPlaying
    )
}
