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

/**
 * 疑似コマ送りを同時に1件だけ受け付けるための状態管理。
 *
 * ジェスチャー側から渡される累積タップ数を監視し、前のフレームの描画待ち中に
 * 到着した追加タップはキューへ溜めずに破棄する。
 *
 * プレイヤー固有のシークや描画完了検知は呼び出し側が担当する。
 */
class PlayerPseudoFrameStepGate {
    private var direction: Int = 0
    private var lastObservedStepCount: Int = 0

    var isAwaitingCompletion: Boolean = false
        private set

    /**
     * 新しい疑似コマ送りを開始できる場合はtrueを返し、完了待ち状態にする。
     *
     * [stepCount]は同一タップシーケンス内の累積値（1, 2, 3...）を想定する。
     * [direction]は負数を戻し、正数を送りとして扱う。0は受け付けない。
     */
    fun tryBegin(
        stepCount: Int,
        direction: Int
    ): Boolean {
        val normalizedDirection = when {
            direction < 0 -> -1
            direction > 0 -> 1
            else -> return false
        }
        val safeStepCount = stepCount.coerceAtLeast(1)

        if (
            safeStepCount == 1 ||
            this.direction != normalizedDirection
        ) {
            this.direction = normalizedDirection
            lastObservedStepCount = 0
            isAwaitingCompletion = false
        }

        if (safeStepCount <= lastObservedStepCount) {
            return false
        }

        // 描画待ち中でも入力済みの番号は記録し、後から再生しない。
        lastObservedStepCount = safeStepCount
        if (isAwaitingCompletion) {
            return false
        }

        isAwaitingCompletion = true
        return true
    }

    /** 前回受け付けた疑似コマ送りの描画が完了したことを通知する。 */
    fun complete() {
        isAwaitingCompletion = false
    }

    /** タップシーケンスと描画待ち状態を破棄する。 */
    fun reset() {
        direction = 0
        lastObservedStepCount = 0
        isAwaitingCompletion = false
    }
}
