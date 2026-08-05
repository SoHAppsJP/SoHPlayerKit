package jp.sohapps.sohplayerkit.ui.controls

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerTransportControlsTest {
    @Test
    fun durationFormattingMatchesPlayerPanel() {
        assertEquals("0:00", formatPlayerTransportDuration(0L))
        assertEquals("0:05", formatPlayerTransportDuration(5_999L))
        assertEquals("1:01", formatPlayerTransportDuration(61_000L))
        assertEquals("1:02:03", formatPlayerTransportDuration(3_723_000L))
    }

    @Test
    fun seekOffsetFormattingUsesWholeSeconds() {
        assertEquals("5s", formatPlayerTransportSeekOffset(5_999L))
        assertEquals("10s", formatPlayerTransportSeekOffset(10_000L))
    }
}
