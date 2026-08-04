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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.max

/** Common playback speeds used by gesture and player control UIs. */
val PLAYER_PLAYBACK_SPEEDS: List<Float> = listOf(
    0.25f,
    0.5f,
    0.75f,
    1.0f,
    1.25f,
    1.5f,
    1.75f,
    2.0f
)

/**
 * Returns the playback speed reached by moving [step] positions through [speeds].
 *
 * A current value that is not present in [speeds] starts from 1.0x, matching the
 * original player gesture behavior.
 */
fun playerPlaybackSpeedAfterStep(
    currentSpeed: Float,
    step: Int,
    speeds: List<Float> = PLAYER_PLAYBACK_SPEEDS
): Float {
    if (speeds.isEmpty()) {
        return currentSpeed
    }

    val currentIndex = speeds.indexOfFirst { it == currentSpeed }
    val defaultIndex = speeds.indexOfFirst { it == 1.0f }
        .takeIf { it >= 0 }
        ?: 0
    val baseIndex = currentIndex.takeIf { it >= 0 } ?: defaultIndex
    val nextIndex = (baseIndex + step).coerceIn(0, speeds.lastIndex)
    return speeds[nextIndex]
}

/** Formats a player position for gesture feedback. */
fun formatPlayerGestureDuration(positionMs: Long): String {
    val safePositionMs = max(0L, positionMs)
    val totalSeconds = safePositionMs / 1000L
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L

    return if (hours > 0L) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}

/**
 * State shared by gesture feedback text and the horizontal seek progress bar.
 *
 * The state is independent from ExoPlayer and LibVLC. Player implementations only
 * report the resulting value or seek target.
 */
@Stable
class PlayerGestureFeedbackState internal constructor() {
    var text: String by mutableStateOf("")
        private set

    var seekProgressVisible: Boolean by mutableStateOf(false)
        private set

    var seekProgress: Float by mutableFloatStateOf(0.0f)
        private set

    internal var messageVersion: Int by mutableIntStateOf(0)
        private set

    fun showMessage(message: String) {
        text = message
        messageVersion += 1
    }

    fun showBrightness(percent: Int) {
        showMessage("明るさ ${percent.coerceIn(0, 100)}%")
    }

    fun showVolume(percent: Int) {
        showMessage("音量 ${percent.coerceIn(0, 100)}%")
    }

    fun showSpeed(speed: Float) {
        showMessage("速度 ${speed}x")
    }

    fun showZoom(scale: Float) {
        showMessage("ズーム ${(scale.coerceAtLeast(0.0f) * 100f).toInt()}%")
    }

    fun showSeek(positionMs: Long, durationMs: Long) {
        val safePositionMs = if (durationMs > 0L) {
            positionMs.coerceIn(0L, durationMs)
        } else {
            max(0L, positionMs)
        }

        seekProgress = if (durationMs > 0L) {
            (safePositionMs.toFloat() / durationMs.toFloat()).coerceIn(0.0f, 1.0f)
        } else {
            0.0f
        }
        seekProgressVisible = true

        if (durationMs > 0L) {
            showMessage(
                "${formatPlayerGestureDuration(safePositionMs)} / " +
                    "${formatPlayerGestureDuration(durationMs)} " +
                    "(${(seekProgress * 100f).toInt()}%)"
            )
        } else {
            showMessage(formatPlayerGestureDuration(safePositionMs))
        }
    }

    fun clearText() {
        text = ""
        messageVersion += 1
    }

    fun clearSeekProgress() {
        seekProgressVisible = false
        seekProgress = 0.0f
    }

    fun clear() {
        clearText()
        clearSeekProgress()
    }

    internal fun clearTextIfVersion(expectedVersion: Int) {
        if (messageVersion == expectedVersion) {
            text = ""
        }
    }
}

@Composable
fun rememberPlayerGestureFeedbackState(): PlayerGestureFeedbackState {
    return remember { PlayerGestureFeedbackState() }
}

/**
 * Displays gesture feedback text and the horizontal seek progress bar.
 */
@Composable
fun PlayerGestureFeedbackOverlay(
    state: PlayerGestureFeedbackState,
    modifier: Modifier = Modifier,
    messageAutoHideMs: Long = 900L,
    seekProgressBottomPadding: Dp = 86.dp
) {
    LaunchedEffect(state.messageVersion, messageAutoHideMs) {
        val version = state.messageVersion
        if (state.text.isNotBlank() && messageAutoHideMs > 0L) {
            delay(messageAutoHideMs)
            state.clearTextIfVersion(version)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (state.seekProgressVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.84f)
                    .padding(bottom = seekProgressBottomPadding)
                    .background(Color.White.copy(alpha = 0.28f), RoundedCornerShape(6.dp))
                    .height(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(state.seekProgress.coerceIn(0.0f, 1.0f))
                        .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(6.dp))
                )
            }
        }

        if (state.text.isNotBlank()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                BasicText(
                    text = state.text,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}
