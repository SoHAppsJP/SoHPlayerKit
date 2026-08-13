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

import jp.sohapps.sohplayerkit.core.model.PlaybackEndAction
import jp.sohapps.sohplayerkit.core.model.PlayerAspectMode
import jp.sohapps.sohplayerkit.core.model.PlayerColorPreset

/**
 * Engine- and application-neutral callbacks used by [rememberPlayerUiState].
 *
 * The host owns playback operations and persistence. This object only describes how shared player
 * UI state reads externally stored color values and forwards changes back to the host.
 */
data class PlayerUiStateBindings(
    val seekAction: (Long) -> Unit,
    val readColorValues: (() -> PlayerColorValues)? = null,
    val onPlaybackSpeedChanged: (Float) -> Unit = {},
    val onPlaybackEndActionChanged: (PlaybackEndAction) -> Unit = {},
    val onAspectModeChanged: (PlayerAspectMode) -> Unit = {},
    val onCustomAspectWidthChanged: (Float) -> Unit = {},
    val onCustomAspectHeightChanged: (Float) -> Unit = {},
    val onRotationLockedChanged: (Boolean) -> Unit = {},
    val onAvoidCutoutChanged: (Boolean) -> Unit = {},
    val onColorPresetChanged: (PlayerColorPreset) -> Unit = {},
    val onColorBrightnessChanged: (Float) -> Unit = {},
    val onColorContrastChanged: (Float) -> Unit = {},
    val onColorSaturationChanged: (Float) -> Unit = {},
    val onColorGammaChanged: (Float) -> Unit = {},
    val onColorTemperatureChanged: (Float) -> Unit = {}
)
