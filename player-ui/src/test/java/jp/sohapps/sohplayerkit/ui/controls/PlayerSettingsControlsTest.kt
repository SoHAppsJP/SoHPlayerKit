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

import jp.sohapps.sohplayerkit.core.model.PlaybackEndAction
import jp.sohapps.sohplayerkit.core.model.PlayerAspectMode
import jp.sohapps.sohplayerkit.core.model.PlayerColorPreset
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerSettingsControlsTest {
    @Test
    fun playbackEndActionLabelsMatchPlayerUi() {
        assertEquals("停止", playerPlaybackEndActionLabel(PlaybackEndAction.STOP))
        assertEquals("次へ", playerPlaybackEndActionLabel(PlaybackEndAction.NEXT))
        assertEquals("リピート", playerPlaybackEndActionLabel(PlaybackEndAction.REPEAT))
    }

    @Test
    fun aspectAndColorLabelsMatchPlayerUi() {
        assertEquals("原寸", playerAspectModeLabel(PlayerAspectMode.ORIGINAL_100))
        assertEquals("自由", playerAspectModeLabel(PlayerAspectMode.CUSTOM))
        assertEquals("映画風", playerColorPresetLabel(PlayerColorPreset.CINEMA))
        assertEquals("手動", playerColorPresetLabel(PlayerColorPreset.CUSTOM))
    }

    @Test
    fun videoInformationFormattingMatchesPlayerUi() {
        assertEquals("1920x1080", formatPlayerSettingsVideoSize(1920, 1080))
        assertEquals("不明", formatPlayerSettingsVideoSize(0, 1080))
        assertEquals("23.98 fps", formatPlayerSettingsFrameRate(23.976f))
        assertEquals("不明", formatPlayerSettingsFrameRate(0f))
        assertEquals("4.50 Mbps", formatPlayerSettingsBitrate(4_500_000))
        assertEquals("192 kbps", formatPlayerSettingsBitrate(192_000))
    }
}
