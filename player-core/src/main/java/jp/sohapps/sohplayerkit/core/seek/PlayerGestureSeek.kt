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

package jp.sohapps.sohplayerkit.core.seek

import kotlin.math.roundToLong

const val PLAYER_PSEUDO_FRAME_STEP_MS = 33L

/**
 * FPSから疑似1フレーム分の時間をミリ秒で求める。
 *
 * FPSが不明または不正な場合は[fallbackMs]を返す。
 *
 * 可変フレームレート動画では平均・公称FPSを基にした値となるため、
 * 厳密なフレーム単位ではなく疑似コマ送り用の目安として使用する。
 */
fun playerPseudoFrameStepMs(
    frameRate: Float,
    fallbackMs: Long = PLAYER_PSEUDO_FRAME_STEP_MS
): Long {
    val safeFallbackMs = fallbackMs.coerceAtLeast(1L)

    if (!frameRate.isFinite() || frameRate <= 0f) {
        return safeFallbackMs
    }

    return (1000.0 / frameRate.toDouble())
        .roundToLong()
        .coerceAtLeast(1L)
}

private const val PLAYER_HORIZONTAL_DRAG_SEEK_MS_PER_PIXEL = 80.0

/**
 * 基準シーク時間を再生速度に応じて補正する。
 *
 * 不正な再生速度は1倍速として扱う。
 */
fun scaledPlayerSeekMs(
    baseSeekMs: Long,
    playbackSpeed: Float
): Long {
    val effectiveSpeed =
        playbackSpeed.takeIf { it.isFinite() && it > 0f } ?: 1.0f

    return (
            baseSeekMs.toDouble() *
                    effectiveSpeed.toDouble()
            )
        .roundToLong()
        .coerceAtLeast(1L)
}

/**
 * 水平方向のドラッグ量をシーク時間へ変換する。
 */
fun playerHorizontalDragSeekDeltaMs(
    deltaX: Float,
    playbackSpeed: Float
): Long {
    val effectiveSpeed =
        playbackSpeed.takeIf { it.isFinite() && it > 0f } ?: 1.0f

    return (
            deltaX.toDouble() *
                    PLAYER_HORIZONTAL_DRAG_SEEK_MS_PER_PIXEL *
                    effectiveSpeed.toDouble()
            )
        .roundToLong()
}

/**
 * 再生中は速度補正付きシーク、一時停止中は疑似コマ送りを実行する。
 *
 * 現時点では共有モジュール内部用。
 */
internal fun performPlayerDoubleTapSeek(
    isPlaying: Boolean,
    baseSeekMs: Long,
    playbackSpeed: Float,
    onSeek: (Long) -> Unit,
    onStepFrame: () -> Unit
) {
    if (isPlaying) {
        onSeek(
            scaledPlayerSeekMs(
                baseSeekMs = baseSeekMs,
                playbackSpeed = playbackSpeed
            )
        )
    } else {
        onStepFrame()
    }
}