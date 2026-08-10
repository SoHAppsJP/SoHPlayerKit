package jp.sohapps.sohplayerkit.ui.playback

import jp.sohapps.sohplayerkit.core.model.PlayerColorPreset
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerColorStateTest {
    @Test
    fun selectPreset_persistsPresetThenReloadsValues() {
        val calls = mutableListOf<String>()
        var storedValues = values(0.0f)
        val state = PlayerColorState(
            initialPreset = PlayerColorPreset.NORMAL,
            initialValues = storedValues,
            onInteraction = { calls += "interaction" },
            onPresetChanged = { preset ->
                calls += "preset:$preset"
                storedValues = values(1.0f)
            },
            readValues = {
                calls += "read"
                storedValues
            }
        )

        state.selectPreset(PlayerColorPreset.BRIGHT)

        assertEquals(PlayerColorPreset.BRIGHT, state.preset)
        assertEquals(values(1.0f), state.values)
        assertEquals(
            listOf("interaction", "preset:BRIGHT", "read"),
            calls
        )
    }

    @Test
    fun setBrightness_updatesValueBeforeSwitchingToCustom() {
        val calls = mutableListOf<String>()
        val state = PlayerColorState(
            initialPreset = PlayerColorPreset.NORMAL,
            initialValues = values(0.0f),
            onInteraction = { calls += "interaction" },
            onPresetChanged = { calls += "preset:$it" },
            onBrightnessChanged = { calls += "brightness:$it" }
        )

        state.setCustomBrightness(0.25f)

        assertEquals(0.25f, state.brightness, 0.0f)
        assertEquals(PlayerColorPreset.CUSTOM, state.preset)
        assertEquals(
            listOf("interaction", "brightness:0.25", "preset:CUSTOM"),
            calls
        )
    }

    @Test
    fun eachCustomSetter_changesOnlyItsValueAndMarksCustom() {
        val initial = PlayerColorValues(
            brightness = 0.1f,
            contrast = 1.1f,
            saturation = 1.2f,
            gamma = 1.3f,
            temperature = 0.2f
        )
        val state = PlayerColorState(
            initialPreset = PlayerColorPreset.VIVID,
            initialValues = initial
        )

        state.setCustomContrast(1.4f)
        assertEquals(1.4f, state.contrast, 0.0f)
        assertEquals(initial.brightness, state.brightness, 0.0f)
        assertEquals(PlayerColorPreset.CUSTOM, state.preset)

        state.setCustomSaturation(1.5f)
        state.setCustomGamma(0.9f)
        state.setCustomTemperature(-0.1f)

        assertEquals(1.5f, state.saturation, 0.0f)
        assertEquals(0.9f, state.gamma, 0.0f)
        assertEquals(-0.1f, state.temperature, 0.0f)
        assertEquals(PlayerColorPreset.CUSTOM, state.preset)
    }

    @Test
    fun refreshValues_reloadsValuesWithoutChangingPreset() {
        var storedValues = values(0.0f)
        val state = PlayerColorState(
            initialPreset = PlayerColorPreset.CINEMA,
            initialValues = storedValues,
            readValues = { storedValues }
        )
        storedValues = values(2.0f)

        state.refreshValues()

        assertEquals(PlayerColorPreset.CINEMA, state.preset)
        assertEquals(values(2.0f), state.values)
    }

    private fun values(base: Float): PlayerColorValues {
        return PlayerColorValues(
            brightness = base,
            contrast = base + 0.1f,
            saturation = base + 0.2f,
            gamma = base + 0.3f,
            temperature = base + 0.4f
        )
    }
}
