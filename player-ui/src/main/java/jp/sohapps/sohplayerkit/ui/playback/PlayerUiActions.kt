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

/**
 * Playback-engine-neutral actions consumed by [PlayerUiHost].
 *
 * Hosts keep ExoPlayer, LibVLC, playlist, and application-specific operations outside the
 * library while exposing the same transport and double-tap contract to the shared player UI.
 */
data class PlayerUiActions(
    val onDoubleTapLeft: (Int) -> Unit,
    val onDoubleTapCenter: () -> Unit,
    val onDoubleTapRight: (Int) -> Unit,
    val onPreviousClick: () -> Unit,
    val onSeekToStartClick: () -> Unit,
    val onSeekBackClick: () -> Unit,
    val onPlayPauseClick: () -> Unit,
    val onSeekForwardClick: () -> Unit,
    val onSeekToEndClick: () -> Unit,
    val onNextClick: () -> Unit
)
