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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

const val PLAYER_RESUME_PREPARING_STATUS_TEXT: String = "再生位置を準備中…"

/**
 * Common state for status text shown above the player surface.
 *
 * The state does not depend on ExoPlayer or LibVLC. Player implementations only report
 * the current status string through [update].
 */
@Stable
class PlayerStatusState internal constructor(
    initialText: String = ""
) {
    var text: String by mutableStateOf(initialText)
        private set

    val isVisible: Boolean
        get() = text.isNotBlank()

    val isResumePreparing: Boolean
        get() = text == PLAYER_RESUME_PREPARING_STATUS_TEXT

    fun update(status: String) {
        text = status
    }

    fun clear() {
        text = ""
    }
}

@Composable
fun rememberPlayerStatusState(
    key: Any? = Unit,
    initialText: String = ""
): PlayerStatusState {
    return remember(key) {
        PlayerStatusState(initialText = initialText)
    }
}

/**
 * Displays the current player status.
 *
 * Resume preparation remains a blocking full-screen black overlay, matching the original
 * player behavior. Other non-empty status strings are shown as a centered translucent message.
 */
@Composable
fun PlayerStatusOverlay(
    state: PlayerStatusState,
    modifier: Modifier = Modifier
) {
    if (!state.isVisible) return

    if (state.isResumePreparing) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            StatusMessage(text = state.text)
        }
        return
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        StatusMessage(text = state.text)
    }
}

@Composable
private fun StatusMessage(text: String) {
    Box(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(12.dp)
    ) {
        Text(
            text = text,
            color = Color.White
        )
    }
}
