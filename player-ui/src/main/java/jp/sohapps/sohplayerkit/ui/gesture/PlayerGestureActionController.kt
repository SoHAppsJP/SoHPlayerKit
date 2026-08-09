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

import android.content.Context
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import jp.sohapps.sohplayerkit.ui.system.PlayerBrightnessController
import jp.sohapps.sohplayerkit.ui.system.PlayerVolumeController

/**
 * Connects vertical player gestures to system brightness, system volume, and playback speed.
 *
 * Playback-engine-specific speed application is supplied by [onPlaybackSpeedChanged], keeping
 * this controller independent from ExoPlayer and LibVLC.
 */
@Stable
class PlayerGestureActionController internal constructor(
    private val changeBrightnessBySteps: ((Int) -> Int)?,
    private val changeVolumeBySteps: (Int) -> Int,
    private val currentPlaybackSpeed: () -> Float,
    private val onPlaybackSpeedChanged: (Float) -> Unit,
    private val feedbackState: PlayerGestureFeedbackState
) {
    fun changeBrightness(step: Int) {
        val percent = changeBrightnessBySteps?.invoke(step) ?: return
        feedbackState.showBrightness(percent)
    }

    fun changeVolume(step: Int) {
        val percent = changeVolumeBySteps(step)
        feedbackState.showVolume(percent)
    }

    fun changeSpeed(step: Int) {
        val current = currentPlaybackSpeed()
        val nextSpeed = playerPlaybackSpeedAfterStep(current, step)
        if (nextSpeed != current) {
            onPlaybackSpeedChanged(nextSpeed)
        }
        feedbackState.showSpeed(nextSpeed)
    }
}

/**
 * Creates a [PlayerGestureActionController] backed by Android window brightness and media volume.
 */
@Composable
fun rememberPlayerGestureActionController(
    context: Context,
    window: Window?,
    playbackSpeed: () -> Float,
    onPlaybackSpeedChanged: (Float) -> Unit,
    feedbackState: PlayerGestureFeedbackState
): PlayerGestureActionController {
    val volumeController = remember(context) {
        PlayerVolumeController(context)
    }
    val brightnessController = remember(window) {
        window?.let(::PlayerBrightnessController)
    }
    val latestPlaybackSpeed = rememberUpdatedState(playbackSpeed)
    val latestOnPlaybackSpeedChanged = rememberUpdatedState(onPlaybackSpeedChanged)

    return remember(volumeController, brightnessController, feedbackState) {
        PlayerGestureActionController(
            changeBrightnessBySteps = brightnessController?.let { controller ->
                { step -> controller.changeBySteps(step) }
            },
            changeVolumeBySteps = { step -> volumeController.changeBySteps(step) },
            currentPlaybackSpeed = { latestPlaybackSpeed.value.invoke() },
            onPlaybackSpeedChanged = { speed ->
                latestOnPlaybackSpeedChanged.value.invoke(speed)
            },
            feedbackState = feedbackState
        )
    }
}
