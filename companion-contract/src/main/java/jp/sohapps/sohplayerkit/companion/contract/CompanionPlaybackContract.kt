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
    const val EXTRA_CAN_NAVIGATE_PREVIOUS = "jp.sohapps.sohplayerkit.companion.extra.CAN_NAVIGATE_PREVIOUS"
    const val EXTRA_CAN_NAVIGATE_NEXT = "jp.sohapps.sohplayerkit.companion.extra.CAN_NAVIGATE_NEXT"
    const val EXTRA_POSITION_MS = "jp.sohapps.sohplayerkit.companion.extra.POSITION_MS"
    const val EXTRA_RESULT_ACTION = "jp.sohapps.sohplayerkit.companion.extra.RESULT_ACTION"

    private const val EXTRA_HAS_PLAYER_SETTINGS =
        "jp.sohapps.sohplayerkit.companion.extra.HAS_PLAYER_SETTINGS"
    private const val EXTRA_BUTTON_SEEK_BACK_MS =
        "jp.sohapps.sohplayerkit.companion.extra.BUTTON_SEEK_BACK_MS"
    private const val EXTRA_BUTTON_SEEK_FORWARD_MS =
        "jp.sohapps.sohplayerkit.companion.extra.BUTTON_SEEK_FORWARD_MS"
    private const val EXTRA_DOUBLE_TAP_SEEK_BACK_MS =
        "jp.sohapps.sohplayerkit.companion.extra.DOUBLE_TAP_SEEK_BACK_MS"
    private const val EXTRA_DOUBLE_TAP_SEEK_FORWARD_MS =
        "jp.sohapps.sohplayerkit.companion.extra.DOUBLE_TAP_SEEK_FORWARD_MS"
    private const val EXTRA_CONTROLS_AUTO_HIDE_MS =
        "jp.sohapps.sohplayerkit.companion.extra.CONTROLS_AUTO_HIDE_MS"
    private const val EXTRA_PLAYBACK_SPEED =
        "jp.sohapps.sohplayerkit.companion.extra.PLAYBACK_SPEED"
    private const val EXTRA_PLAYBACK_END_ACTION =
        "jp.sohapps.sohplayerkit.companion.extra.PLAYBACK_END_ACTION"
    private const val EXTRA_ASPECT_MODE =
        "jp.sohapps.sohplayerkit.companion.extra.ASPECT_MODE"
    private const val EXTRA_CUSTOM_ASPECT_WIDTH =
        "jp.sohapps.sohplayerkit.companion.extra.CUSTOM_ASPECT_WIDTH"
    private const val EXTRA_CUSTOM_ASPECT_HEIGHT =
        "jp.sohapps.sohplayerkit.companion.extra.CUSTOM_ASPECT_HEIGHT"
    private const val EXTRA_ROTATION_LOCKED =
        "jp.sohapps.sohplayerkit.companion.extra.ROTATION_LOCKED"
    private const val EXTRA_AVOID_CUTOUT =
        "jp.sohapps.sohplayerkit.companion.extra.AVOID_CUTOUT"
    private const val EXTRA_COLOR_PRESET =
        "jp.sohapps.sohplayerkit.companion.extra.COLOR_PRESET"
    private const val EXTRA_COLOR_BRIGHTNESS =
        "jp.sohapps.sohplayerkit.companion.extra.COLOR_BRIGHTNESS"
    private const val EXTRA_COLOR_CONTRAST =
        "jp.sohapps.sohplayerkit.companion.extra.COLOR_CONTRAST"
    private const val EXTRA_COLOR_SATURATION =
        "jp.sohapps.sohplayerkit.companion.extra.COLOR_SATURATION"
    private const val EXTRA_COLOR_GAMMA =
        "jp.sohapps.sohplayerkit.companion.extra.COLOR_GAMMA"
    private const val EXTRA_COLOR_TEMPERATURE =
        "jp.sohapps.sohplayerkit.companion.extra.COLOR_TEMPERATURE"

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
            putExtra(EXTRA_CAN_NAVIGATE_PREVIOUS, request.canNavigatePrevious)
            putExtra(EXTRA_CAN_NAVIGATE_NEXT, request.canNavigateNext)
            request.playerSettings?.let { putPlayerSettings(it) }
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
                videoFps = intent.positiveFiniteFloatExtraOrNull(EXTRA_VIDEO_FPS),
                canNavigatePrevious = intent.getBooleanExtra(EXTRA_CAN_NAVIGATE_PREVIOUS, false),
                canNavigateNext = intent.getBooleanExtra(EXTRA_CAN_NAVIGATE_NEXT, false),
                playerSettings = intent.playerSettingsOrNull()
            )
        }.getOrNull()
    }

    fun createResultIntent(result: CompanionPlaybackResult): Intent {
        return Intent().apply {
            putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION)
            putExtra(EXTRA_POSITION_MS, result.positionMs)
            putExtra(EXTRA_RESULT_ACTION, result.action.name)
            result.durationMs?.let { putExtra(EXTRA_DURATION_MS, it) }
            result.playerSettings?.let { putPlayerSettings(it) }
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
        val action = intent.getStringExtra(EXTRA_RESULT_ACTION)
            ?.let { encoded ->
                runCatching { CompanionPlaybackResultAction.valueOf(encoded) }.getOrNull()
            }
            ?: CompanionPlaybackResultAction.RETURN_TO_LIST

        return runCatching {
            CompanionPlaybackResult(
                positionMs = positionMs,
                durationMs = intent.positiveLongExtraOrNull(EXTRA_DURATION_MS),
                action = action,
                playerSettings = intent.playerSettingsOrNull()
            )
        }.getOrNull()
    }

    private fun Intent.putPlayerSettings(settings: CompanionPlayerSettingsSnapshot) {
        putExtra(EXTRA_HAS_PLAYER_SETTINGS, true)
        putExtra(EXTRA_BUTTON_SEEK_BACK_MS, settings.buttonSeekBackMs)
        putExtra(EXTRA_BUTTON_SEEK_FORWARD_MS, settings.buttonSeekForwardMs)
        putExtra(EXTRA_DOUBLE_TAP_SEEK_BACK_MS, settings.doubleTapSeekBackMs)
        putExtra(EXTRA_DOUBLE_TAP_SEEK_FORWARD_MS, settings.doubleTapSeekForwardMs)
        putExtra(EXTRA_CONTROLS_AUTO_HIDE_MS, settings.controlsAutoHideMs)
        putExtra(EXTRA_PLAYBACK_SPEED, settings.playbackSpeed)
        putExtra(EXTRA_PLAYBACK_END_ACTION, settings.playbackEndAction)
        putExtra(EXTRA_ASPECT_MODE, settings.aspectMode)
        putExtra(EXTRA_CUSTOM_ASPECT_WIDTH, settings.customAspectWidth)
        putExtra(EXTRA_CUSTOM_ASPECT_HEIGHT, settings.customAspectHeight)
        putExtra(EXTRA_ROTATION_LOCKED, settings.rotationLocked)
        putExtra(EXTRA_AVOID_CUTOUT, settings.avoidCutout)
        putExtra(EXTRA_COLOR_PRESET, settings.colorPreset)
        putExtra(EXTRA_COLOR_BRIGHTNESS, settings.colorBrightness)
        putExtra(EXTRA_COLOR_CONTRAST, settings.colorContrast)
        putExtra(EXTRA_COLOR_SATURATION, settings.colorSaturation)
        putExtra(EXTRA_COLOR_GAMMA, settings.colorGamma)
        putExtra(EXTRA_COLOR_TEMPERATURE, settings.colorTemperature)
    }

    private fun Intent.playerSettingsOrNull(): CompanionPlayerSettingsSnapshot? {
        if (!getBooleanExtra(EXTRA_HAS_PLAYER_SETTINGS, false)) {
            return null
        }

        val buttonSeekBackMs = nonNegativeLongExtraOrNull(EXTRA_BUTTON_SEEK_BACK_MS) ?: return null
        val buttonSeekForwardMs = nonNegativeLongExtraOrNull(EXTRA_BUTTON_SEEK_FORWARD_MS) ?: return null
        val doubleTapSeekBackMs = nonNegativeLongExtraOrNull(EXTRA_DOUBLE_TAP_SEEK_BACK_MS) ?: return null
        val doubleTapSeekForwardMs = nonNegativeLongExtraOrNull(EXTRA_DOUBLE_TAP_SEEK_FORWARD_MS) ?: return null
        val controlsAutoHideMs = nonNegativeLongExtraOrNull(EXTRA_CONTROLS_AUTO_HIDE_MS) ?: return null
        val playbackSpeed = positiveFiniteFloatExtraOrNull(EXTRA_PLAYBACK_SPEED) ?: return null
        val playbackEndAction = getStringExtra(EXTRA_PLAYBACK_END_ACTION)
            ?.takeIf { it.isNotBlank() }
            ?: return null
        val aspectMode = getStringExtra(EXTRA_ASPECT_MODE)
            ?.takeIf { it.isNotBlank() }
            ?: return null
        val customAspectWidth = positiveFiniteFloatExtraOrNull(EXTRA_CUSTOM_ASPECT_WIDTH) ?: return null
        val customAspectHeight = positiveFiniteFloatExtraOrNull(EXTRA_CUSTOM_ASPECT_HEIGHT) ?: return null
        val colorPreset = getStringExtra(EXTRA_COLOR_PRESET)
            ?.takeIf { it.isNotBlank() }
            ?: return null
        val colorBrightness = finiteFloatExtraOrNull(EXTRA_COLOR_BRIGHTNESS) ?: return null
        val colorContrast = finiteFloatExtraOrNull(EXTRA_COLOR_CONTRAST) ?: return null
        val colorSaturation = finiteFloatExtraOrNull(EXTRA_COLOR_SATURATION) ?: return null
        val colorGamma = finiteFloatExtraOrNull(EXTRA_COLOR_GAMMA) ?: return null
        val colorTemperature = finiteFloatExtraOrNull(EXTRA_COLOR_TEMPERATURE) ?: return null

        return runCatching {
            CompanionPlayerSettingsSnapshot(
                buttonSeekBackMs = buttonSeekBackMs,
                buttonSeekForwardMs = buttonSeekForwardMs,
                doubleTapSeekBackMs = doubleTapSeekBackMs,
                doubleTapSeekForwardMs = doubleTapSeekForwardMs,
                controlsAutoHideMs = controlsAutoHideMs,
                playbackSpeed = playbackSpeed,
                playbackEndAction = playbackEndAction,
                aspectMode = aspectMode,
                customAspectWidth = customAspectWidth,
                customAspectHeight = customAspectHeight,
                rotationLocked = getBooleanExtra(EXTRA_ROTATION_LOCKED, false),
                avoidCutout = getBooleanExtra(EXTRA_AVOID_CUTOUT, true),
                colorPreset = colorPreset,
                colorBrightness = colorBrightness,
                colorContrast = colorContrast,
                colorSaturation = colorSaturation,
                colorGamma = colorGamma,
                colorTemperature = colorTemperature
            )
        }.getOrNull()
    }

    private fun Intent.positiveLongExtraOrNull(key: String): Long? {
        if (!hasExtra(key)) {
            return null
        }
        return getLongExtra(key, -1L).takeIf { it > 0L }
    }

    private fun Intent.nonNegativeLongExtraOrNull(key: String): Long? {
        if (!hasExtra(key)) {
            return null
        }
        return getLongExtra(key, -1L).takeIf { it >= 0L }
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

    private fun Intent.finiteFloatExtraOrNull(key: String): Float? {
        if (!hasExtra(key)) {
            return null
        }
        return getFloatExtra(key, Float.NaN).takeIf { it.isFinite() }
    }
}
