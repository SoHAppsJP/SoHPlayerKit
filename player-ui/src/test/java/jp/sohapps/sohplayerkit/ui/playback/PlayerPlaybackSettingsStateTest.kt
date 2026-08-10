package jp.sohapps.sohplayerkit.ui.playback

import jp.sohapps.sohplayerkit.core.model.PlaybackEndAction
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerPlaybackSettingsStateTest {
    @Test
    fun updatePlaybackSpeed_doesNotReportControlsInteraction() {
        val events = mutableListOf<String>()
        val state = createState(
            onInteraction = { events += "interaction" },
            onPlaybackSpeedChanged = { events += "speed:$it" }
        )

        state.updatePlaybackSpeed(1.5f)

        assertEquals(1.5f, state.playbackSpeed)
        assertEquals(listOf("speed:1.5"), events)
    }

    @Test
    fun selectPlaybackSpeed_reportsInteractionBeforeApplyingSpeed() {
        val events = mutableListOf<String>()
        val state = createState(
            onInteraction = { events += "interaction" },
            onPlaybackSpeedChanged = { events += "speed:$it" }
        )

        state.selectPlaybackSpeed(0.75f)

        assertEquals(0.75f, state.playbackSpeed)
        assertEquals(listOf("interaction", "speed:0.75"), events)
    }

    @Test
    fun selectPlaybackEndAction_updatesStateAndPersistsAfterInteraction() {
        val events = mutableListOf<String>()
        val state = createState(
            onInteraction = { events += "interaction" },
            onPlaybackEndActionChanged = { events += "end:$it" }
        )

        state.selectPlaybackEndAction(PlaybackEndAction.REPEAT)

        assertEquals(PlaybackEndAction.REPEAT, state.playbackEndAction)
        assertEquals(listOf("interaction", "end:REPEAT"), events)
    }

    @Test
    fun selectingSameSpeed_stillAppliesItLikeControlsPanelDid() {
        val events = mutableListOf<String>()
        val state = createState(
            onInteraction = { events += "interaction" },
            onPlaybackSpeedChanged = { events += "speed:$it" }
        )

        state.selectPlaybackSpeed(1.0f)

        assertEquals(1.0f, state.playbackSpeed)
        assertEquals(listOf("interaction", "speed:1.0"), events)
    }

    private fun createState(
        onInteraction: () -> Unit = {},
        onPlaybackSpeedChanged: (Float) -> Unit = {},
        onPlaybackEndActionChanged: (PlaybackEndAction) -> Unit = {}
    ): PlayerPlaybackSettingsState {
        return PlayerPlaybackSettingsState(
            initialPlaybackSpeed = 1.0f,
            initialPlaybackEndAction = PlaybackEndAction.STOP,
            onInteraction = onInteraction,
            onPlaybackSpeedChanged = onPlaybackSpeedChanged,
            onPlaybackEndActionChanged = onPlaybackEndActionChanged
        )
    }
}
