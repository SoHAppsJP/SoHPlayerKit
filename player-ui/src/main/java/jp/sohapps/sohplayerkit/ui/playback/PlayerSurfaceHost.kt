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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import jp.sohapps.sohplayerkit.ui.system.playerDisplayCutoutModifier

/**
 * Shared root frame for a player surface.
 *
 * Playback engines provide their own video output and overlays through [content]. This host keeps
 * the common black background, display-cutout policy, and clipping behavior in one place without
 * adding another layout node beyond the [Box] that player surfaces already require.
 */
@Composable
fun PlayerSurfaceHost(
    state: PlayerUiState,
    window: Window?,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val cutoutModifier = playerDisplayCutoutModifier(
        window = window,
        avoidCutoutInLandscape = state.displayState.avoidCutout
    )

    Box(
        modifier = modifier
            .background(Color.Black)
            .then(cutoutModifier)
            .clipToBounds(),
        content = content
    )
}
