package jp.sohapps.sohplayerkit.ui.playback

import jp.sohapps.sohplayerkit.core.model.PlayerAspectMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerDisplaySettingsStateTest {
    @Test
    fun selectAspectMode_updatesStateAndPersistsAfterInteraction() {
        val events = mutableListOf<String>()
        val state = createState(
            onInteraction = { events += "interaction" },
            onAspectModeChanged = { events += "aspect:$it" }
        )

        state.selectAspectMode(PlayerAspectMode.ZOOM)

        assertEquals(PlayerAspectMode.ZOOM, state.aspectMode)
        assertEquals(
            listOf("interaction", "aspect:ZOOM"),
            events
        )
    }

    @Test
    fun validCustomWidth_persistsWidthThenSelectsCustom() {
        val events = mutableListOf<String>()
        val state = createState(
            onInteraction = { events += "interaction" },
            onAspectModeChanged = { events += "aspect:$it" },
            onCustomAspectWidthChanged = { events += "width:$it" }
        )

        state.updateCustomAspectWidthText("21")

        assertEquals("21", state.customAspectWidthText)
        assertEquals(21f, state.customAspectWidth)
        assertEquals(PlayerAspectMode.CUSTOM, state.aspectMode)
        assertEquals(
            listOf("interaction", "width:21.0", "aspect:CUSTOM"),
            events
        )
    }

    @Test
    fun invalidCustomWidth_keepsLastValidValueAndAspectMode() {
        val events = mutableListOf<String>()
        val state = createState(
            onInteraction = { events += "interaction" },
            onAspectModeChanged = { events += "aspect:$it" },
            onCustomAspectWidthChanged = { events += "width:$it" }
        )

        state.updateCustomAspectWidthText("")

        assertEquals("", state.customAspectWidthText)
        assertEquals(16f, state.customAspectWidth)
        assertEquals(PlayerAspectMode.FIT, state.aspectMode)
        assertEquals(listOf("interaction"), events)
    }

    @Test
    fun validCustomHeight_persistsHeightThenSelectsCustom() {
        val events = mutableListOf<String>()
        val state = createState(
            onInteraction = { events += "interaction" },
            onAspectModeChanged = { events += "aspect:$it" },
            onCustomAspectHeightChanged = { events += "height:$it" }
        )

        state.updateCustomAspectHeightText("10.5")

        assertEquals("10.5", state.customAspectHeightText)
        assertEquals(10.5f, state.customAspectHeight)
        assertEquals(PlayerAspectMode.CUSTOM, state.aspectMode)
        assertEquals(
            listOf("interaction", "height:10.5", "aspect:CUSTOM"),
            events
        )
    }

    @Test
    fun toggles_persistNewValues() {
        val events = mutableListOf<String>()
        val state = createState(
            onInteraction = { events += "interaction" },
            onRotationLockedChanged = { events += "rotation:$it" },
            onAvoidCutoutChanged = { events += "cutout:$it" }
        )

        state.toggleRotationLocked()
        state.toggleAvoidCutout()

        assertTrue(state.rotationLocked)
        assertFalse(state.avoidCutout)
        assertEquals(
            listOf(
                "interaction",
                "rotation:true",
                "interaction",
                "cutout:false"
            ),
            events
        )
    }

    private fun createState(
        onInteraction: () -> Unit = {},
        onAspectModeChanged: (PlayerAspectMode) -> Unit = {},
        onCustomAspectWidthChanged: (Float) -> Unit = {},
        onCustomAspectHeightChanged: (Float) -> Unit = {},
        onRotationLockedChanged: (Boolean) -> Unit = {},
        onAvoidCutoutChanged: (Boolean) -> Unit = {}
    ): PlayerDisplaySettingsState {
        return PlayerDisplaySettingsState(
            initialAspectMode = PlayerAspectMode.FIT,
            initialCustomAspectWidth = 16f,
            initialCustomAspectHeight = 9f,
            initialRotationLocked = false,
            initialAvoidCutout = true,
            onInteraction = onInteraction,
            onAspectModeChanged = onAspectModeChanged,
            onCustomAspectWidthChanged = onCustomAspectWidthChanged,
            onCustomAspectHeightChanged = onCustomAspectHeightChanged,
            onRotationLockedChanged = onRotationLockedChanged,
            onAvoidCutoutChanged = onAvoidCutoutChanged
        )
    }
}
