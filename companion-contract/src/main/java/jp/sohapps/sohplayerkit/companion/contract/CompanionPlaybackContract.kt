package jp.sohapps.sohplayerkit.companion.contract

import android.content.Intent

object CompanionPlaybackContract {
    const val PROTOCOL_VERSION = 1

    const val ACTION_PLAY = "jp.sohapps.sohplayerkit.companion.action.PLAY"

    const val EXTRA_PROTOCOL_VERSION = "jp.sohapps.sohplayerkit.companion.extra.PROTOCOL_VERSION"
    const val EXTRA_DISPLAY_NAME = "jp.sohapps.sohplayerkit.companion.extra.DISPLAY_NAME"
    const val EXTRA_RESUME_POSITION_MS = "jp.sohapps.sohplayerkit.companion.extra.RESUME_POSITION_MS"
    const val EXTRA_DURATION_MS = "jp.sohapps.sohplayerkit.companion.extra.DURATION_MS"
    const val EXTRA_VIDEO_WIDTH = "jp.sohapps.sohplayerkit.companion.extra.VIDEO_WIDTH"
    const val EXTRA_VIDEO_HEIGHT = "jp.sohapps.sohplayerkit.companion.extra.VIDEO_HEIGHT"
    const val EXTRA_VIDEO_FPS = "jp.sohapps.sohplayerkit.companion.extra.VIDEO_FPS"
    const val EXTRA_POSITION_MS = "jp.sohapps.sohplayerkit.companion.extra.POSITION_MS"

    fun createPlayIntent(request: CompanionPlaybackRequest): Intent {
        return Intent(ACTION_PLAY).apply {
            setDataAndType(request.mediaUri, request.mimeType)
            putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION)
            putExtra(EXTRA_DISPLAY_NAME, request.displayName)
            putExtra(EXTRA_RESUME_POSITION_MS, request.resumePositionMs)
            request.durationMs?.let { putExtra(EXTRA_DURATION_MS, it) }
            request.videoWidth?.let { putExtra(EXTRA_VIDEO_WIDTH, it) }
            request.videoHeight?.let { putExtra(EXTRA_VIDEO_HEIGHT, it) }
            request.videoFps?.let { putExtra(EXTRA_VIDEO_FPS, it) }
        }
    }

    fun parsePlayRequest(intent: Intent): CompanionPlaybackRequest? {
        if (intent.action != ACTION_PLAY) {
            return null
        }
        if (intent.getIntExtra(EXTRA_PROTOCOL_VERSION, -1) != PROTOCOL_VERSION) {
            return null
        }

        val mediaUri = intent.data ?: return null
        val mimeType = intent.type?.takeIf { it.isNotBlank() } ?: return null
        val displayName = intent.getStringExtra(EXTRA_DISPLAY_NAME)
            ?.takeIf { it.isNotBlank() }
            ?: return null
        val resumePositionMs = intent.getLongExtra(EXTRA_RESUME_POSITION_MS, 0L)
            .takeIf { it >= 0L }
            ?: return null

        return runCatching {
            CompanionPlaybackRequest(
                mediaUri = mediaUri,
                mimeType = mimeType,
                displayName = displayName,
                resumePositionMs = resumePositionMs,
                durationMs = intent.positiveLongExtraOrNull(EXTRA_DURATION_MS),
                videoWidth = intent.positiveIntExtraOrNull(EXTRA_VIDEO_WIDTH),
                videoHeight = intent.positiveIntExtraOrNull(EXTRA_VIDEO_HEIGHT),
                videoFps = intent.positiveFiniteFloatExtraOrNull(EXTRA_VIDEO_FPS)
            )
        }.getOrNull()
    }

    fun createResultIntent(result: CompanionPlaybackResult): Intent {
        return Intent().apply {
            putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION)
            putExtra(EXTRA_POSITION_MS, result.positionMs)
            result.durationMs?.let { putExtra(EXTRA_DURATION_MS, it) }
        }
    }

    fun parseResult(intent: Intent?): CompanionPlaybackResult? {
        if (intent == null) {
            return null
        }
        if (intent.getIntExtra(EXTRA_PROTOCOL_VERSION, -1) != PROTOCOL_VERSION) {
            return null
        }
        if (!intent.hasExtra(EXTRA_POSITION_MS)) {
            return null
        }

        val positionMs = intent.getLongExtra(EXTRA_POSITION_MS, -1L)
            .takeIf { it >= 0L }
            ?: return null

        return runCatching {
            CompanionPlaybackResult(
                positionMs = positionMs,
                durationMs = intent.positiveLongExtraOrNull(EXTRA_DURATION_MS)
            )
        }.getOrNull()
    }

    private fun Intent.positiveLongExtraOrNull(key: String): Long? {
        if (!hasExtra(key)) {
            return null
        }
        return getLongExtra(key, -1L).takeIf { it > 0L }
    }

    private fun Intent.positiveIntExtraOrNull(key: String): Int? {
        if (!hasExtra(key)) {
            return null
        }
        return getIntExtra(key, -1).takeIf { it > 0 }
    }

    private fun Intent.positiveFiniteFloatExtraOrNull(key: String): Float? {
        if (!hasExtra(key)) {
            return null
        }
        return getFloatExtra(key, Float.NaN).takeIf { it.isFinite() && it > 0f }
    }
}
