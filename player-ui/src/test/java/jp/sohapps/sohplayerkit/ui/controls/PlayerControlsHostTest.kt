package jp.sohapps.sohplayerkit.ui.controls

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerControlsHostTest {
    @Test
    fun transportInteraction_showsControlsBeforeAction() {
        val events = mutableListOf<String>()
        val state = PlayerControlsVisibilityState()

        performPlayerControlsInteraction(state) {
            events += "action"
            assertTrue(state.visible)
        }

        assertTrue(state.visible)
        assertEquals(1, state.interactionVersion)
        assertEquals(listOf("action"), events)
    }

    @Test
    fun hiddenControls_arePremeasuredOnlyOnceBeforeFirstDisplay() {
        assertTrue(
            shouldPremeasurePlayerControls(
                visible = false,
                premeasured = false
            )
        )
        assertEquals(
            false,
            shouldPremeasurePlayerControls(
                visible = false,
                premeasured = true
            )
        )
        assertEquals(
            false,
            shouldPremeasurePlayerControls(
                visible = true,
                premeasured = false
            )
        )
    }

    @Test
    fun warmedHiddenControls_areDeactivatedButVisibleControlsStayActive() {
        assertTrue(
            shouldKeepPlayerControlsContentActive(
                visible = false,
                premeasured = false
            )
        )
        assertEquals(
            false,
            shouldKeepPlayerControlsContentActive(
                visible = false,
                premeasured = true
            )
        )
        assertTrue(
            shouldKeepPlayerControlsContentActive(
                visible = true,
                premeasured = true
            )
        )
    }

    @Test
    fun sliderSeek_showsControlsThenClearsFeedbackThenSeeks() {
        val events = mutableListOf<String>()
        val state = PlayerControlsVisibilityState()

        performPlayerControlsSeek(
            controlsState = state,
            positionMs = 12_345L,
            force = true,
            onClearTransientFeedback = {
                assertTrue(state.visible)
                events += "clear"
            },
            onSeek = { position, force ->
                assertTrue(state.visible)
                events += "seek:$position:$force"
            }
        )

        assertEquals(1, state.interactionVersion)
        assertEquals(
            listOf("clear", "seek:12345:true"),
            events
        )
    }
}
