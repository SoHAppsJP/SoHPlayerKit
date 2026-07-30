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

package jp.sohapps.sohplayerkit.core.model

/**
 * 動画の表示方法。
 *
 * 定数名は設定の保存値として利用される可能性があるため、
 * 互換性を維持する目的で安易に変更しないこと。
 */
enum class PlayerAspectMode {
    FIT,
    FILL,
    ZOOM,
    ORIGINAL_100,
    RATIO_16_9,
    RATIO_4_3,
    CUSTOM
}

/**
 * 動画の再生終了後に行う動作。
 */
enum class PlaybackEndAction {
    STOP,
    NEXT,
    REPEAT
}

/**
 * 動画へ適用する色補正プリセット。
 */
enum class PlayerColorPreset {
    NORMAL,
    BRIGHT,
    VIVID,
    CINEMA,
    NIGHT,
    SOFT,
    CUSTOM
}

/**
 * プレイヤーエンジンから取得した動画・音声情報。
 *
 * ExoPlayerやLibVLCなど、特定の再生エンジンには依存しない。
 */
data class PlaybackVideoInfo(
    val videoCodec: String? = null,
    val audioCodec: String? = null,
    val videoBitrate: Int? = null,
    val audioBitrate: Int? = null,
    val audioTrackCount: Int? = null,
    val subtitleTrackCount: Int? = null
)