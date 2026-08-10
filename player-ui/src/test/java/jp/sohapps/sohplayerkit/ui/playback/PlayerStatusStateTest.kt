package jp.sohapps.sohplayerkit.ui.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerStatusStateTest {
    @Test
    fun initialState_isHidden() {
        val state = PlayerStatusState()

        assertEquals("", state.text)
        assertFalse(state.isVisible)
        assertFalse(state.isResumePreparing)
    }

    @Test
    fun updateResumePreparing_marksStatusAsBlockingPreparation() {
        val state = PlayerStatusState()

        state.update(PLAYER_RESUME_PREPARING_STATUS_TEXT)

        assertTrue(state.isVisible)
        assertTrue(state.isResumePreparing)
        assertEquals(PLAYER_RESUME_PREPARING_STATUS_TEXT, state.text)
    }

    @Test
    fun updateNormalMessage_keepsStatusVisibleWithoutPreparationFlag() {
        val state = PlayerStatusState()

        state.update("VLC再生でエラーが発生しました")

        assertTrue(state.isVisible)
        assertFalse(state.isResumePreparing)
        assertEquals("VLC再生でエラーが発生しました", state.text)
    }

    @Test
    fun clear_hidesStatus() {
        val state = PlayerStatusState(initialText = PLAYER_RESUME_PREPARING_STATUS_TEXT)

        state.clear()

        assertEquals("", state.text)
        assertFalse(state.isVisible)
        assertFalse(state.isResumePreparing)
    }
}
