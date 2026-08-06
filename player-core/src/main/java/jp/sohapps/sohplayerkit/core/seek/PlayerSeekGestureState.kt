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

import kotlinx.coroutines.CoroutineScope

/**
 * 横ドラッグとシークバーのプレビューシークをまとめて扱う状態クラス。
 *
 * 現在位置、長さ、再生速度は呼び出し時にプロバイダーから取得するため、Composeや
 * 特定の再生エンジンには依存しない。連続シークは[CoalescingSeekController]で間引き、
 * ジェスチャー終了時には最後のプレビュー位置を強制シークとして確定する。
 */
class PlayerSeekGestureState(
    scope: CoroutineScope,
    private val currentPositionMs: () -> Long,
    private val durationMs: () -> Long,
    private val playbackSpeed: () -> Float,
    private val onPreviewPositionChanged: (Long) -> Unit,
    private val onSeekFeedback: (positionMs: Long, durationMs: Long) -> Unit,
    minIntervalMs: Long = 260L,
    seekAction: (Long) -> Unit
) {
    private val coalescingSeekController = CoalescingSeekController(
        scope = scope,
        minIntervalMs = minIntervalMs,
        seekAction = seekAction
    )

    private var dragPreviewPositionMs: Long? = null

    /**
     * 指定位置へプレビューシークする。
     *
     * 長さが分かる場合は0から長さまで、分からない場合は0以上へ補正する。
     * 補正後の位置を返す。
     */
    fun seekTo(
        positionMs: Long,
        force: Boolean,
        showFeedback: Boolean = true
    ): Long {
        val duration = durationMs()
        val clampedPosition = clampPosition(positionMs, duration)
        onPreviewPositionChanged(clampedPosition)
        coalescingSeekController.seekTo(clampedPosition, force)
        if (showFeedback) {
            onSeekFeedback(clampedPosition, duration)
        }
        return clampedPosition
    }

    /** 横方向の移動量を再生速度に応じたシーク量へ変換し、プレビュー位置を更新する。 */
    fun dragBy(deltaX: Float): Long {
        val seekDeltaMs = playerHorizontalDragSeekDeltaMs(deltaX, playbackSpeed())
        val basePosition = dragPreviewPositionMs ?: currentPositionMs()
        val targetPosition = clampPosition(basePosition + seekDeltaMs, durationMs())
        dragPreviewPositionMs = targetPosition
        return seekTo(
            positionMs = targetPosition,
            force = false,
            showFeedback = true
        )
    }

    /** 最後のドラッグプレビュー位置を強制シークとして確定する。 */
    fun commitDrag(): Long? {
        val targetPosition = dragPreviewPositionMs ?: return null
        dragPreviewPositionMs = null
        return seekTo(
            positionMs = targetPosition,
            force = true,
            showFeedback = true
        )
    }

    /** 確定シークを行わず、ドラッグ中のプレビュー位置だけを破棄する。 */
    fun cancelDrag() {
        dragPreviewPositionMs = null
    }

    internal fun hasDragPreview(): Boolean = dragPreviewPositionMs != null

    private fun clampPosition(positionMs: Long, durationMs: Long): Long {
        return if (durationMs > 0L) {
            positionMs.coerceIn(0L, durationMs)
        } else {
            positionMs.coerceAtLeast(0L)
        }
    }
}
