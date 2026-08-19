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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.sohapps.sohplayerkit.core.model.PlaybackEndAction
import jp.sohapps.sohplayerkit.core.model.PlaybackVideoInfo
import jp.sohapps.sohplayerkit.core.model.PlayerAspectMode
import jp.sohapps.sohplayerkit.core.model.PlayerColorPreset
import jp.sohapps.sohplayerkit.ui.gesture.PLAYER_PLAYBACK_SPEEDS

/** Menu currently displayed below [PlayerSettingsControls]. */
enum class PlayerMenu {
    NONE,
    SPEED,
    END_ACTION,
    ASPECT,
    INFO,
    COLOR
}

/** Drawable resource IDs used by [PlayerSettingsControls]. */
data class PlayerSettingsIcons(
    val speed: Int,
    val repeat: Int,
    val aspect: Int,
    val info: Int,
    val color: Int,
    val rotation: Int,
    val rotationLock: Int,
    val notch: Int
)

/**
 * Engine-neutral player settings row and its expandable menus.
 *
 * The host owns settings persistence and playback-engine operations. This component owns only
 * the shared layout, labels, selectable values, information presentation, and editing controls.
 */
@Composable
fun PlayerSettingsControls(
    menu: PlayerMenu,
    playbackSpeed: Float,
    playbackEndAction: PlaybackEndAction,
    aspectMode: PlayerAspectMode,
    colorPreset: PlayerColorPreset,
    rotationLocked: Boolean,
    avoidCutout: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    videoWidth: Int,
    videoHeight: Int,
    videoRotationDegrees: Int?,
    videoPixelRatio: Float?,
    videoFrameRate: Float,
    playbackVideoInfo: PlaybackVideoInfo,
    customAspectWidthText: String,
    customAspectHeightText: String,
    colorBrightness: Float,
    colorContrast: Float,
    colorSaturation: Float,
    colorGamma: Float,
    colorTemperature: Float,
    icons: PlayerSettingsIcons,
    onMenuClick: (PlayerMenu) -> Unit,
    onPlaybackSpeedSelected: (Float) -> Unit,
    onPlaybackEndActionSelected: (PlaybackEndAction) -> Unit,
    onAspectModeSelected: (PlayerAspectMode) -> Unit,
    onColorPresetSelected: (PlayerColorPreset) -> Unit,
    onRotationLockClick: () -> Unit,
    onAvoidCutoutClick: () -> Unit,
    onCustomAspectWidthChange: (String) -> Unit,
    onCustomAspectHeightChange: (String) -> Unit,
    onColorBrightnessChange: (Float) -> Unit,
    onColorContrastChange: (Float) -> Unit,
    onColorSaturationChange: (Float) -> Unit,
    onColorGammaChange: (Float) -> Unit,
    onColorTemperatureChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            PlayerSettingsIconButton(
                iconRes = icons.speed,
                value = "${playbackSpeed}x",
                modifier = Modifier.weight(1f)
            ) {
                onMenuClick(PlayerMenu.SPEED)
            }
            PlayerSettingsIconButton(
                iconRes = icons.repeat,
                value = playerPlaybackEndActionLabel(playbackEndAction),
                modifier = Modifier.weight(1f)
            ) {
                onMenuClick(PlayerMenu.END_ACTION)
            }
            PlayerSettingsIconButton(
                iconRes = icons.aspect,
                value = playerAspectModeLabel(aspectMode),
                modifier = Modifier.weight(1f)
            ) {
                onMenuClick(PlayerMenu.ASPECT)
            }
            PlayerSettingsIconButton(
                iconRes = icons.info,
                value = "Video",
                modifier = Modifier.weight(1f)
            ) {
                onMenuClick(PlayerMenu.INFO)
            }
            PlayerSettingsIconButton(
                iconRes = icons.color,
                value = playerColorPresetLabel(colorPreset),
                modifier = Modifier.weight(1f)
            ) {
                onMenuClick(PlayerMenu.COLOR)
            }
            PlayerSettingsIconButton(
                iconRes = if (rotationLocked) icons.rotationLock else icons.rotation,
                value = if (rotationLocked) "Lock" else "Auto",
                modifier = Modifier.weight(1f),
                onClick = onRotationLockClick
            )
            PlayerSettingsIconButton(
                iconRes = icons.notch,
                value = if (avoidCutout) "回避" else "全画面",
                modifier = Modifier.weight(1f),
                onClick = onAvoidCutoutClick
            )
        }

        when (menu) {
            PlayerMenu.NONE -> Unit
            PlayerMenu.SPEED -> PlayerSpeedMenu(playbackSpeed, onPlaybackSpeedSelected)
            PlayerMenu.END_ACTION -> PlayerEndActionMenu(
                playbackEndAction,
                onPlaybackEndActionSelected
            )
            PlayerMenu.ASPECT -> PlayerAspectMenu(
                selectedMode = aspectMode,
                customAspectWidthText = customAspectWidthText,
                customAspectHeightText = customAspectHeightText,
                onSelected = onAspectModeSelected,
                onCustomAspectWidthChange = onCustomAspectWidthChange,
                onCustomAspectHeightChange = onCustomAspectHeightChange
            )
            PlayerMenu.INFO -> PlayerVideoInfoMenu(
                currentPositionMs = currentPositionMs,
                durationMs = durationMs,
                videoWidth = videoWidth,
                videoHeight = videoHeight,
                videoRotationDegrees = videoRotationDegrees,
                videoPixelRatio = videoPixelRatio,
                videoFrameRate = videoFrameRate,
                playbackVideoInfo = playbackVideoInfo
            )
            PlayerMenu.COLOR -> PlayerColorPresetMenu(
                selectedPreset = colorPreset,
                brightness = colorBrightness,
                contrast = colorContrast,
                saturation = colorSaturation,
                gamma = colorGamma,
                temperature = colorTemperature,
                onPresetSelected = onColorPresetSelected,
                onBrightnessChange = onColorBrightnessChange,
                onContrastChange = onColorContrastChange,
                onSaturationChange = onColorSaturationChange,
                onGammaChange = onColorGammaChange,
                onTemperatureChange = onColorTemperatureChange
            )
        }
    }
}

@Composable
private fun PlayerColorPresetMenu(
    selectedPreset: PlayerColorPreset,
    brightness: Float,
    contrast: Float,
    saturation: Float,
    gamma: Float,
    temperature: Float,
    onPresetSelected: (PlayerColorPreset) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onContrastChange: (Float) -> Unit,
    onSaturationChange: (Float) -> Unit,
    onGammaChange: (Float) -> Unit,
    onTemperatureChange: (Float) -> Unit
) {
    PlayerMenuSection(title = "色補正") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PlayerSelectableChip("標準", selectedPreset == PlayerColorPreset.NORMAL) {
                onPresetSelected(PlayerColorPreset.NORMAL)
            }
            PlayerSelectableChip("明るく", selectedPreset == PlayerColorPreset.BRIGHT) {
                onPresetSelected(PlayerColorPreset.BRIGHT)
            }
            PlayerSelectableChip("色濃く", selectedPreset == PlayerColorPreset.VIVID) {
                onPresetSelected(PlayerColorPreset.VIVID)
            }
            PlayerSelectableChip("映画風", selectedPreset == PlayerColorPreset.CINEMA) {
                onPresetSelected(PlayerColorPreset.CINEMA)
            }
            PlayerSelectableChip("夜向け", selectedPreset == PlayerColorPreset.NIGHT) {
                onPresetSelected(PlayerColorPreset.NIGHT)
            }
            PlayerSelectableChip("やわらか", selectedPreset == PlayerColorPreset.SOFT) {
                onPresetSelected(PlayerColorPreset.SOFT)
            }
            PlayerSelectableChip("手動", selectedPreset == PlayerColorPreset.CUSTOM) {
                onPresetSelected(PlayerColorPreset.CUSTOM)
            }
        }
        PlayerColorSlider("明るさ", brightness, -0.5f, 0.5f, onBrightnessChange)
        PlayerColorSlider("コントラスト", contrast, 0.5f, 2.0f, onContrastChange)
        PlayerColorSlider("彩度", saturation, 0.0f, 2.0f, onSaturationChange)
        PlayerColorSlider("ガンマ", gamma, 0.5f, 2.0f, onGammaChange)
        PlayerColorSlider("色温度", temperature, -1.0f, 1.0f, onTemperatureChange)
    }
}

@Composable
private fun PlayerColorSlider(
    label: String,
    value: Float,
    minimum: Float,
    maximum: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, color = Color.White, fontSize = 12.sp)
            Text(formatPlayerSettingsFloat(value), color = Color.White, fontSize = 12.sp)
        }
        Slider(
            value = value.coerceIn(minimum, maximum),
            onValueChange = onValueChange,
            valueRange = minimum..maximum,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PlayerSpeedMenu(
    selectedSpeed: Float,
    onSelected: (Float) -> Unit
) {
    PlayerMenuSection(title = "再生速度") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PLAYER_PLAYBACK_SPEEDS.forEach { speed ->
                PlayerSelectableChip("${speed}x", selectedSpeed == speed) {
                    onSelected(speed)
                }
            }
        }
    }
}

@Composable
private fun PlayerEndActionMenu(
    selectedAction: PlaybackEndAction,
    onSelected: (PlaybackEndAction) -> Unit
) {
    PlayerMenuSection(title = "再生終了時") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PlayerSelectableChip("停止", selectedAction == PlaybackEndAction.STOP) {
                onSelected(PlaybackEndAction.STOP)
            }
            PlayerSelectableChip("次へ", selectedAction == PlaybackEndAction.NEXT) {
                onSelected(PlaybackEndAction.NEXT)
            }
            PlayerSelectableChip("リピート", selectedAction == PlaybackEndAction.REPEAT) {
                onSelected(PlaybackEndAction.REPEAT)
            }
        }
    }
}

@Composable
private fun PlayerAspectMenu(
    selectedMode: PlayerAspectMode,
    customAspectWidthText: String,
    customAspectHeightText: String,
    onSelected: (PlayerAspectMode) -> Unit,
    onCustomAspectWidthChange: (String) -> Unit,
    onCustomAspectHeightChange: (String) -> Unit
) {
    PlayerMenuSection(title = "画面サイズ / 比率") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PlayerSelectableChip("最適化", selectedMode == PlayerAspectMode.FIT) {
                onSelected(PlayerAspectMode.FIT)
            }
            PlayerSelectableChip("フル", selectedMode == PlayerAspectMode.FILL) {
                onSelected(PlayerAspectMode.FILL)
            }
            PlayerSelectableChip("クロップ", selectedMode == PlayerAspectMode.ZOOM) {
                onSelected(PlayerAspectMode.ZOOM)
            }
            PlayerSelectableChip("原寸100%", selectedMode == PlayerAspectMode.ORIGINAL_100) {
                onSelected(PlayerAspectMode.ORIGINAL_100)
            }
            PlayerSelectableChip("16:9", selectedMode == PlayerAspectMode.RATIO_16_9) {
                onSelected(PlayerAspectMode.RATIO_16_9)
            }
            PlayerSelectableChip("4:3", selectedMode == PlayerAspectMode.RATIO_4_3) {
                onSelected(PlayerAspectMode.RATIO_4_3)
            }
            PlayerSelectableChip("自由比率", selectedMode == PlayerAspectMode.CUSTOM) {
                onSelected(PlayerAspectMode.CUSTOM)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            PlayerSettingsTextField(
                value = customAspectWidthText,
                label = "横",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp),
                onValueChange = onCustomAspectWidthChange
            )
            Text(":", color = Color.White)
            PlayerSettingsTextField(
                value = customAspectHeightText,
                label = "縦",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp),
                onValueChange = onCustomAspectHeightChange
            )
        }
        Text(
            text = "16:9 / 4:3 / 自由比率は指定比率の表示枠を作り、その枠内にフル表示します。",
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun PlayerVideoInfoMenu(
    currentPositionMs: Long,
    durationMs: Long,
    videoWidth: Int,
    videoHeight: Int,
    videoRotationDegrees: Int?,
    videoPixelRatio: Float?,
    videoFrameRate: Float,
    playbackVideoInfo: PlaybackVideoInfo
) {
    PlayerMenuSection(title = "ビデオ情報") {
        playbackVideoInfo.fileName?.takeIf { it.isNotBlank() }?.let { fileName ->
            Text("ファイル名: $fileName", color = Color.White, fontSize = 12.sp)
        }
        Text(
            "解像度: ${formatPlayerSettingsVideoSize(videoWidth, videoHeight)}",
            color = Color.White,
            fontSize = 12.sp
        )
        Text(
            "FPS: ${formatPlayerSettingsFrameRate(videoFrameRate)}",
            color = Color.White,
            fontSize = 12.sp
        )
        playbackVideoInfo.videoCodec?.let { codec ->
            Text("映像コーデック: $codec", color = Color.White, fontSize = 12.sp)
        }
        playbackVideoInfo.audioCodec?.let { codec ->
            Text("音声コーデック: $codec", color = Color.White, fontSize = 12.sp)
        }
        playbackVideoInfo.videoBitrate?.takeIf { it > 0 }?.let { bitrate ->
            Text(
                "映像ビットレート: ${formatPlayerSettingsBitrate(bitrate)}",
                color = Color.White,
                fontSize = 12.sp
            )
        }
        playbackVideoInfo.audioBitrate?.takeIf { it > 0 }?.let { bitrate ->
            Text(
                "音声ビットレート: ${formatPlayerSettingsBitrate(bitrate)}",
                color = Color.White,
                fontSize = 12.sp
            )
        }
        playbackVideoInfo.audioTrackCount?.let { count ->
            Text("音声トラック: $count", color = Color.White, fontSize = 12.sp)
        }
        playbackVideoInfo.subtitleTrackCount?.let { count ->
            Text("字幕トラック: $count", color = Color.White, fontSize = 12.sp)
        }
        Text(
            "回転メタデータ: ${videoRotationDegrees?.let { "${it}°" } ?: "不明"}",
            color = Color.White,
            fontSize = 12.sp
        )
        Text(
            "ピクセル比率: ${videoPixelRatio?.let(::formatPlayerSettingsFloat) ?: "不明"}",
            color = Color.White,
            fontSize = 12.sp
        )
        Text(
            "現在位置: ${formatPlayerTransportDuration(currentPositionMs)}",
            color = Color.White,
            fontSize = 12.sp
        )
        Text(
            "長さ: ${durationMs.takeIf { it > 0L }?.let(::formatPlayerTransportDuration) ?: "不明"}",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun PlayerMenuSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(Color.White.copy(alpha = 0.08f), androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
            .padding(10.dp)
    ) {
        Text(
            title,
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        content()
    }
}

@Composable
private fun PlayerSelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                if (selected) "✓ $text" else text,
                color = Color.White
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) {
                Color.White.copy(alpha = 0.24f)
            } else {
                Color.White.copy(alpha = 0.10f)
            },
            labelColor = Color.White
        )
    )
}

@Composable
private fun PlayerSettingsTextField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label, color = Color.White.copy(alpha = 0.85f))
        },
        singleLine = true,
        textStyle = TextStyle(color = Color.White),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.75f),
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
            cursorColor = Color.White
        ),
        modifier = modifier
    )
}

@Composable
private fun PlayerSettingsIconButton(
    iconRes: Int,
    value: String,
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
            contentDescription = value,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = value,
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

internal fun playerPlaybackEndActionLabel(action: PlaybackEndAction): String {
    return when (action) {
        PlaybackEndAction.STOP -> "停止"
        PlaybackEndAction.NEXT -> "次へ"
        PlaybackEndAction.REPEAT -> "リピート"
    }
}

internal fun playerAspectModeLabel(mode: PlayerAspectMode): String {
    return when (mode) {
        PlayerAspectMode.FIT -> "最適化"
        PlayerAspectMode.FILL -> "フル"
        PlayerAspectMode.ZOOM -> "クロップ"
        PlayerAspectMode.ORIGINAL_100 -> "原寸"
        PlayerAspectMode.RATIO_16_9 -> "16:9"
        PlayerAspectMode.RATIO_4_3 -> "4:3"
        PlayerAspectMode.CUSTOM -> "自由"
    }
}

internal fun playerColorPresetLabel(preset: PlayerColorPreset): String {
    return when (preset) {
        PlayerColorPreset.NORMAL -> "標準"
        PlayerColorPreset.BRIGHT -> "明るく"
        PlayerColorPreset.VIVID -> "色濃く"
        PlayerColorPreset.CINEMA -> "映画風"
        PlayerColorPreset.NIGHT -> "夜向け"
        PlayerColorPreset.SOFT -> "柔らか"
        PlayerColorPreset.CUSTOM -> "手動"
    }
}

internal fun formatPlayerSettingsVideoSize(width: Int, height: Int): String {
    return if (width > 0 && height > 0) "${width}x${height}" else "不明"
}

internal fun formatPlayerSettingsFrameRate(frameRate: Float): String {
    return if (frameRate > 0f) {
        "${formatPlayerSettingsFloat(frameRate)} fps"
    } else {
        "不明"
    }
}

internal fun formatPlayerSettingsBitrate(bitsPerSecond: Int): String {
    return if (bitsPerSecond >= 1_000_000) {
        "${formatPlayerSettingsFloat(bitsPerSecond / 1_000_000f)} Mbps"
    } else {
        "${bitsPerSecond / 1000} kbps"
    }
}

internal fun formatPlayerSettingsFloat(value: Float): String = "%.2f".format(value)
