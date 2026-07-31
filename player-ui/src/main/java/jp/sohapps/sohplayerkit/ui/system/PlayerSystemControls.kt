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

package jp.sohapps.sohplayerkit.ui.system

import android.content.Context
import android.media.AudioManager
import android.view.Window

/**
 * Controls Android's music-stream volume independently of the playback engine.
 */
class PlayerVolumeController(
    context: Context
) {
    private val audioManager: AudioManager = requireNotNull(
        context.applicationContext.getSystemService(AudioManager::class.java)
    )

    /**
     * Changes the music-stream volume by Android volume steps and returns 0..100 percent.
     */
    fun changeBySteps(
        stepCount: Int,
        showSystemUi: Boolean = true
    ): Int {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val nextVolume = (currentVolume + stepCount).coerceIn(0, maxVolume)
        val flags = if (showSystemUi) AudioManager.FLAG_SHOW_UI else 0

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            nextVolume,
            flags
        )

        return if (maxVolume > 0) {
            nextVolume * 100 / maxVolume
        } else {
            0
        }
    }
}

/**
 * Controls only the supplied player's [Window] brightness.
 *
 * This does not change the device-wide brightness setting.
 */
class PlayerBrightnessController(
    private val window: Window,
    private val stepSize: Float = DEFAULT_STEP_SIZE,
    private val minimumBrightness: Float = DEFAULT_MINIMUM_BRIGHTNESS,
    private val inheritedBrightnessFallback: Float = DEFAULT_INHERITED_BRIGHTNESS_FALLBACK
) {
    /**
     * Changes window brightness by logical steps and returns 0..100 percent.
     */
    fun changeBySteps(stepCount: Int): Int {
        val attributes = window.attributes
        val currentBrightness = attributes.screenBrightness
            .takeIf { it >= 0f }
            ?: inheritedBrightnessFallback
        val nextBrightness = (currentBrightness + stepCount * stepSize)
            .coerceIn(minimumBrightness, 1.0f)

        attributes.screenBrightness = nextBrightness
        window.attributes = attributes

        return (nextBrightness * 100f).toInt()
    }

    companion object {
        private const val DEFAULT_STEP_SIZE = 0.05f
        private const val DEFAULT_MINIMUM_BRIGHTNESS = 0.05f
        private const val DEFAULT_INHERITED_BRIGHTNESS_FALLBACK = 0.5f
    }
}
