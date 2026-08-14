package jp.sohapps.sohplayerkit.companion.contract

import android.content.Intent
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CompanionPlaybackContractInstrumentedTest {
    @Test
    fun playRequest_roundTripsRequiredAndOptionalValues() {
        val request = CompanionPlaybackRequest(
            mediaUri = Uri.parse("http://127.0.0.1:49876/playback/token/video.mp4"),
            mimeType = "video/mp4",
            displayName = "video.mp4",
            resumePositionMs = 20_626L,
            durationMs = 2_500_000L,
            videoWidth = 1280,
            videoHeight = 720,
            videoFps = 29.97f
        )

        val parsed = CompanionPlaybackContract.parsePlayRequest(
            CompanionPlaybackContract.createPlayIntent(request)
        )

        assertEquals(request, parsed)
    }

    @Test
    fun playRequest_roundTripsWithoutOptionalMetadata() {
        val request = CompanionPlaybackRequest(
            mediaUri = Uri.parse("http://127.0.0.1:49876/playback/token/video.ogm"),
            mimeType = "video/ogg",
            displayName = "video.ogm"
        )

        val parsed = CompanionPlaybackContract.parsePlayRequest(
            CompanionPlaybackContract.createPlayIntent(request)
        )

        assertEquals(request, parsed)
    }

    @Test
    fun playRequest_rejectsUnsupportedProtocolVersion() {
        val intent = CompanionPlaybackContract.createPlayIntent(
            CompanionPlaybackRequest(
                mediaUri = Uri.parse("http://127.0.0.1:49876/playback/token/video.mp4"),
                mimeType = "video/mp4",
                displayName = "video.mp4"
            )
        ).putExtra(
            CompanionPlaybackContract.EXTRA_PROTOCOL_VERSION,
            CompanionPlaybackContract.PROTOCOL_VERSION + 1
        )

        assertNull(CompanionPlaybackContract.parsePlayRequest(intent))
    }

    @Test
    fun playRequest_rejectsMissingMediaUri() {
        val intent = Intent(CompanionPlaybackContract.ACTION_PLAY).apply {
            type = "video/mp4"
            putExtra(
                CompanionPlaybackContract.EXTRA_PROTOCOL_VERSION,
                CompanionPlaybackContract.PROTOCOL_VERSION
            )
            putExtra(CompanionPlaybackContract.EXTRA_DISPLAY_NAME, "video.mp4")
        }

        assertNull(CompanionPlaybackContract.parsePlayRequest(intent))
    }

    @Test
    fun result_roundTripsFinalPositionAndDuration() {
        val result = CompanionPlaybackResult(
            positionMs = 2_493_000L,
            durationMs = 2_500_000L
        )

        val parsed = CompanionPlaybackContract.parseResult(
            CompanionPlaybackContract.createResultIntent(result)
        )

        assertEquals(result, parsed)
    }

    @Test
    fun result_rejectsMissingPosition() {
        val intent = Intent().apply {
            putExtra(
                CompanionPlaybackContract.EXTRA_PROTOCOL_VERSION,
                CompanionPlaybackContract.PROTOCOL_VERSION
            )
        }

        assertNull(CompanionPlaybackContract.parseResult(intent))
    }
}
