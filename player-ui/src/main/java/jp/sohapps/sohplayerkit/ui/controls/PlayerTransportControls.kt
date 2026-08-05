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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Drawable resource IDs used by [PlayerTransportControls]. */
data class PlayerTransportIcons(
    val previous: Int,
    val seekToStart: Int,
    val seekBack: Int,
    val play: Int,
    val pause: Int,
    val seekForward: Int,
    val seekToEnd: Int,
    val next: Int
)

/**
 * Engine-neutral playback transport controls shared by the main app and companion players.
 *
 * Resource IDs and playback operations are supplied by the host application. The component
 * owns only the seek-slider interaction, time labels, button layout, and labels.
 */
@Composable
fun PlayerTransportControls(
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    seekBackMs: Long,
    seekForwardMs: Long,
    icons: PlayerTransportIcons,
    onPreviousClick: () -> Unit,
    onSeekToStartClick: () -> Unit,
    onSeekBackClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeekForwardClick: () -> Unit,
    onSeekToEndClick: () -> Unit,
    onNextClick: () -> Unit,
    onSliderValueChange: (Long) -> Unit,
    onSliderValueChangeFinished: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (durationMs > 0L) {
            val sliderDragging = remember { mutableStateOf(false) }
            val sliderValue = remember {
                mutableFloatStateOf(currentPositionMs.coerceIn(0L, durationMs).toFloat())
            }
            LaunchedEffect(currentPositionMs, durationMs) {
                if (!sliderDragging.value) {
                    sliderValue.floatValue = currentPositionMs.coerceIn(0L, durationMs).toFloat()
                }
            }
            Slider(
                value = sliderValue.floatValue.coerceIn(0f, durationMs.toFloat()),
                onValueChange = { value ->
                    sliderDragging.value = true
                    sliderValue.floatValue = value
                    onSliderValueChange(value.toLong())
                },
                onValueChangeFinished = {
                    sliderDragging.value = false
                    onSliderValueChangeFinished(sliderValue.floatValue.toLong())
                },
                valueRange = 0f..durationMs.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = formatPlayerTransportDuration(currentPositionMs),
                color = Color.White,
                fontSize = 12.sp
            )
            Text(
                text = formatPlayerTransportDuration(durationMs),
                color = Color.White,
                fontSize = 12.sp
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            PlayerTransportIconButton(
                iconRes = icons.previous,
                label = "前",
                modifier = Modifier.weight(1f),
                onClick = onPreviousClick
            )
            PlayerTransportIconButton(
                iconRes = icons.seekToStart,
                label = "先頭",
                modifier = Modifier.weight(1f),
                onClick = onSeekToStartClick
            )
            PlayerTransportIconButton(
                iconRes = icons.seekBack,
                label = "-${formatPlayerTransportSeekOffset(seekBackMs)}",
                modifier = Modifier.weight(1f),
                onClick = onSeekBackClick
            )
            PlayerTransportIconButton(
                iconRes = if (isPlaying) icons.pause else icons.play,
                label = if (isPlaying) "停止" else "再生",
                modifier = Modifier.weight(1f),
                onClick = onPlayPauseClick
            )
            PlayerTransportIconButton(
                iconRes = icons.seekForward,
                label = "+${formatPlayerTransportSeekOffset(seekForwardMs)}",
                modifier = Modifier.weight(1f),
                onClick = onSeekForwardClick
            )
            PlayerTransportIconButton(
                iconRes = icons.seekToEnd,
                label = "最後",
                modifier = Modifier.weight(1f),
                onClick = onSeekToEndClick
            )
            PlayerTransportIconButton(
                iconRes = icons.next,
                label = "次",
                modifier = Modifier.weight(1f),
                onClick = onNextClick
            )
        }
    }
}

@Composable
private fun PlayerTransportIconButton(
    iconRes: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 1.dp, vertical = 2.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

internal fun formatPlayerTransportDuration(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val totalSeconds = ms / 1000L
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    return if (hours > 0L) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}

internal fun formatPlayerTransportSeekOffset(ms: Long): String = "${ms / 1000L}s"
