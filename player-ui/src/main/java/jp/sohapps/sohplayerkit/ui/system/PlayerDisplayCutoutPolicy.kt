/*
 * Copyright 2026 SoH Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.sohapps.sohplayerkit.ui.system

import android.content.res.Configuration
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Applies a consistent display-cutout policy to a player surface.
 *
 * Portrait playback always avoids the display cutout. Landscape playback avoids it only when
 * [avoidCutoutInLandscape] is true; otherwise the window is allowed to draw into short-edge
 * cutout areas. The original window cutout mode is restored when this composable leaves the
 * composition.
 */
@Composable
fun playerDisplayCutoutModifier(
    window: Window?,
    avoidCutoutInLandscape: Boolean
): Modifier {
    val orientation = LocalConfiguration.current.orientation
    val shouldAvoidCutout =
        orientation != Configuration.ORIENTATION_LANDSCAPE || avoidCutoutInLandscape
    val originalCutoutMode = remember(window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window?.attributes?.layoutInDisplayCutoutMode
        } else {
            null
        }
    }

    SideEffect {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && window != null) {
            val attributes = window.attributes
            val nextMode = if (shouldAvoidCutout) {
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
            } else {
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            if (attributes.layoutInDisplayCutoutMode != nextMode) {
                attributes.layoutInDisplayCutoutMode = nextMode
                window.attributes = attributes
            }
        }
    }

    DisposableEffect(window, originalCutoutMode) {
        onDispose {
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                window != null &&
                originalCutoutMode != null
            ) {
                val attributes = window.attributes
                if (attributes.layoutInDisplayCutoutMode != originalCutoutMode) {
                    attributes.layoutInDisplayCutoutMode = originalCutoutMode
                    window.attributes = attributes
                }
            }
        }
    }

    return if (shouldAvoidCutout) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Modifier.windowInsetsPadding(
                WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
            )
        } else {
            Modifier.windowInsetsPadding(WindowInsets.displayCutout)
        }
    } else {
        Modifier
    }
}
