package jp.sohapps.sohplayerkit.ui.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import jp.sohapps.sohplayerkit.core.model.PlayerColorPreset

data class PlayerColorValues(
    val brightness: Float,
    val contrast: Float,
    val saturation: Float,
    val gamma: Float,
    val temperature: Float
)

@Stable
class PlayerColorState(
    initialPreset: PlayerColorPreset,
    initialValues: PlayerColorValues,
    private val onInteraction: () -> Unit = {},
    private val onPresetChanged: (PlayerColorPreset) -> Unit = {},
    private val readValues: () -> PlayerColorValues = { initialValues },
    private val onBrightnessChanged: (Float) -> Unit = {},
    private val onContrastChanged: (Float) -> Unit = {},
    private val onSaturationChanged: (Float) -> Unit = {},
    private val onGammaChanged: (Float) -> Unit = {},
    private val onTemperatureChanged: (Float) -> Unit = {}
) {
    var preset by mutableStateOf(initialPreset)
        private set

    var brightness by mutableFloatStateOf(initialValues.brightness)
        private set

    var contrast by mutableFloatStateOf(initialValues.contrast)
        private set

    var saturation by mutableFloatStateOf(initialValues.saturation)
        private set

    var gamma by mutableFloatStateOf(initialValues.gamma)
        private set

    var temperature by mutableFloatStateOf(initialValues.temperature)
        private set

    val values: PlayerColorValues
        get() = PlayerColorValues(
            brightness = brightness,
            contrast = contrast,
            saturation = saturation,
            gamma = gamma,
            temperature = temperature
        )

    fun selectPreset(nextPreset: PlayerColorPreset) {
        onInteraction()
        preset = nextPreset
        onPresetChanged(nextPreset)
        replaceValues(readValues())
    }

    fun setCustomBrightness(value: Float) {
        setCustomValue(
            value = value,
            updateState = { brightness = it },
            persistValue = onBrightnessChanged
        )
    }

    fun setCustomContrast(value: Float) {
        setCustomValue(
            value = value,
            updateState = { contrast = it },
            persistValue = onContrastChanged
        )
    }

    fun setCustomSaturation(value: Float) {
        setCustomValue(
            value = value,
            updateState = { saturation = it },
            persistValue = onSaturationChanged
        )
    }

    fun setCustomGamma(value: Float) {
        setCustomValue(
            value = value,
            updateState = { gamma = it },
            persistValue = onGammaChanged
        )
    }

    fun setCustomTemperature(value: Float) {
        setCustomValue(
            value = value,
            updateState = { temperature = it },
            persistValue = onTemperatureChanged
        )
    }

    fun refreshValues() {
        replaceValues(readValues())
    }

    private fun setCustomValue(
        value: Float,
        updateState: (Float) -> Unit,
        persistValue: (Float) -> Unit
    ) {
        onInteraction()
        updateState(value)
        persistValue(value)
        preset = PlayerColorPreset.CUSTOM
        onPresetChanged(PlayerColorPreset.CUSTOM)
    }

    private fun replaceValues(values: PlayerColorValues) {
        brightness = values.brightness
        contrast = values.contrast
        saturation = values.saturation
        gamma = values.gamma
        temperature = values.temperature
    }
}

@Composable
fun rememberPlayerColorState(
    key: Any? = Unit,
    initialPreset: PlayerColorPreset,
    initialValues: PlayerColorValues,
    onInteraction: () -> Unit = {},
    onPresetChanged: (PlayerColorPreset) -> Unit = {},
    readValues: () -> PlayerColorValues = { initialValues },
    onBrightnessChanged: (Float) -> Unit = {},
    onContrastChanged: (Float) -> Unit = {},
    onSaturationChanged: (Float) -> Unit = {},
    onGammaChanged: (Float) -> Unit = {},
    onTemperatureChanged: (Float) -> Unit = {}
): PlayerColorState {
    return remember(key) {
        PlayerColorState(
            initialPreset = initialPreset,
            initialValues = initialValues,
            onInteraction = onInteraction,
            onPresetChanged = onPresetChanged,
            readValues = readValues,
            onBrightnessChanged = onBrightnessChanged,
            onContrastChanged = onContrastChanged,
            onSaturationChanged = onSaturationChanged,
            onGammaChanged = onGammaChanged,
            onTemperatureChanged = onTemperatureChanged
        )
    }
}
