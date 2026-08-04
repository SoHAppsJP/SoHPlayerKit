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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 連続して届くシーク要求を間引き、未処理分は最新位置だけを保持する。
 *
 * プレイヤーやデータソースが短時間に大量のシークを処理しないように、最初の要求は
 * 即時実行し、その後は[minIntervalMs]ごとに最新の保留位置だけを[seekAction]へ渡す。
 * [seekTo]へ`force = true`を渡した場合は、保留中の位置を破棄して最終位置を即時実行する。
 *
 * 呼び出しと[seekAction]は、同一スレッドまたは同一ディスパッチャー上で扱うことを想定する。
 */
class CoalescingSeekController(
    private val scope: CoroutineScope,
    private val minIntervalMs: Long = 260L,
    private val seekAction: (Long) -> Unit
) {
    private var inFlight = false
    private var pendingPositionMs: Long? = null
    private var generation = 0

    fun seekTo(positionMs: Long, force: Boolean) {
        if (force) {
            generation += 1
            pendingPositionMs = null
            inFlight = true
            seekAction(positionMs)
            scheduleNext(generation)
            return
        }

        if (!inFlight) {
            inFlight = true
            seekAction(positionMs)
            scheduleNext(generation)
        } else {
            pendingPositionMs = positionMs
        }
    }

    private fun scheduleNext(activeGeneration: Int) {
        scope.launch {
            delay(minIntervalMs)
            if (activeGeneration != generation) {
                return@launch
            }

            val next = pendingPositionMs
            if (next == null) {
                inFlight = false
                return@launch
            }

            pendingPositionMs = null
            seekAction(next)
            scheduleNext(activeGeneration)
        }
    }
}
