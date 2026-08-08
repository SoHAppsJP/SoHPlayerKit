package jp.sohapps.sohplayerkit.ui.playback

import jp.sohapps.sohplayerkit.core.model.PlaybackVideoInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerVideoMetadataStateTest {
    @Test
    fun replace_replacesUnknownValuesToo() {
        val state = PlayerVideoMetadataState(
            initialWidth = 1920,
            initialHeight = 1080,
            initialRotationDegrees = 90,
            initialPixelRatio = 1.5f,
            initialFrameRate = 60.0f
        )

        state.replace(
            width = 0,
            height = 0,
            rotationDegrees = null,
            pixelRatio = 0.0f,
            frameRate = 0.0f,
            playbackVideoInfo = PlaybackVideoInfo()
        )

        assertEquals(0, state.width)
        assertEquals(0, state.height)
        assertNull(state.rotationDegrees)
        assertEquals(0.0f, state.pixelRatio)
        assertEquals(0.0f, state.frameRate, 0.0f)
        assertFalse(state.hasVideoSize)
    }

    @Test
    fun mergeKnown_preservesKnownValuesWhenUpdateIsUnknown() {
        val state = PlayerVideoMetadataState(
            initialWidth = 1920,
            initialHeight = 1080,
            initialRotationDegrees = 90,
            initialPixelRatio = 1.5f,
            initialFrameRate = 60.0f
        )

        state.mergeKnown(
            width = 0,
            height = -1,
            rotationDegrees = null,
            pixelRatio = Float.NaN,
            frameRate = 0.0f,
            playbackVideoInfo = PlaybackVideoInfo()
        )

        assertEquals(1920, state.width)
        assertEquals(1080, state.height)
        assertEquals(90, state.rotationDegrees)
        assertEquals(1.5f, state.pixelRatio)
        assertEquals(60.0f, state.frameRate, 0.0f)
        assertTrue(state.hasVideoSize)
    }

    @Test
    fun mergeKnown_updatesValidValues() {
        val state = PlayerVideoMetadataState()

        state.mergeKnown(
            width = 1280,
            height = 720,
            rotationDegrees = 270,
            pixelRatio = 1.0f,
            frameRate = 29.97f,
            playbackVideoInfo = PlaybackVideoInfo()
        )

        assertEquals(1280, state.width)
        assertEquals(720, state.height)
        assertEquals(270, state.rotationDegrees)
        assertEquals(1.0f, state.pixelRatio)
        assertEquals(29.97f, state.frameRate, 0.001f)
        assertTrue(state.hasVideoSize)
    }

    @Test
    fun fallbackAccessors_returnLayoutDefaultsWhenUnknown() {
        val state = PlayerVideoMetadataState()

        assertEquals(0, state.rotationDegreesOrZero)
        assertEquals(1.0f, state.pixelRatioOrOne, 0.0f)
    }
}
