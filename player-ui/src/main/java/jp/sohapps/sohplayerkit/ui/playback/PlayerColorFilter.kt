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

package jp.sohapps.sohplayerkit.ui.playback

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import kotlin.math.abs
import kotlin.math.max

/** Applies the shared player color controls directly to a View-backed video output. */
fun applyPlayerColorFilter(
    view: View,
    brightness: Float,
    contrast: Float,
    saturation: Float,
    gamma: Float,
    temperature: Float
) {
    val safeGamma = gamma.coerceAtLeast(0.01f)
    if (isNeutralPlayerColor(brightness, contrast, saturation, safeGamma, temperature)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(null)
        }
        view.setLayerType(View.LAYER_TYPE_NONE, null)
        return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        applyRuntimeShaderColorFilter(
            view = view,
            brightness = brightness,
            contrast = contrast,
            saturation = saturation,
            gamma = safeGamma,
            temperature = temperature
        )
    } else {
        applyColorMatrixFallback(
            view = view,
            brightness = brightness,
            contrast = contrast,
            saturation = saturation,
            gamma = safeGamma,
            temperature = temperature
        )
    }
}

private fun isNeutralPlayerColor(
    brightness: Float,
    contrast: Float,
    saturation: Float,
    gamma: Float,
    temperature: Float
): Boolean {
    return abs(brightness) < 0.0001f &&
        abs(contrast - 1f) < 0.0001f &&
        abs(saturation - 1f) < 0.0001f &&
        abs(gamma - 1f) < 0.0001f &&
        abs(temperature) < 0.0001f
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun applyRuntimeShaderColorFilter(
    view: View,
    brightness: Float,
    contrast: Float,
    saturation: Float,
    gamma: Float,
    temperature: Float
) {
    val shader = RuntimeShader(PLAYER_COLOR_SHADER)
    shader.setFloatUniform("brightness", brightness)
    shader.setFloatUniform("contrast", contrast)
    shader.setFloatUniform("saturation", saturation)
    shader.setFloatUniform("gammaValue", gamma)
    shader.setFloatUniform("temperature", temperature)
    view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    view.setRenderEffect(RenderEffect.createRuntimeShaderEffect(shader, "content"))
}

private fun applyColorMatrixFallback(
    view: View,
    brightness: Float,
    contrast: Float,
    saturation: Float,
    gamma: Float,
    temperature: Float
) {
    val result = ColorMatrix()

    val saturationMatrix = ColorMatrix().apply { setSaturation(saturation) }
    result.postConcat(saturationMatrix)

    val gammaGain = 1f / max(gamma, 0.01f)
    val redTemperature = 1f + temperature * 0.15f
    val blueTemperature = 1f - temperature * 0.15f
    val translation = (brightness * 255f) + (128f * (1f - contrast))
    val adjustment = ColorMatrix(
        floatArrayOf(
            contrast * gammaGain * redTemperature, 0f, 0f, 0f, translation,
            0f, contrast * gammaGain, 0f, 0f, translation,
            0f, 0f, contrast * gammaGain * blueTemperature, 0f, translation,
            0f, 0f, 0f, 1f, 0f
        )
    )
    result.postConcat(adjustment)

    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(result)
    }
    view.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
}

private const val PLAYER_COLOR_SHADER = """
uniform shader content;
uniform float brightness;
uniform float contrast;
uniform float saturation;
uniform float gammaValue;
uniform float temperature;

half4 main(float2 coordinate) {
    half4 source = content.eval(coordinate);
    float3 rgb = float3(source.rgb);

    rgb = (rgb - 0.5) * contrast + 0.5 + brightness;
    float luma = dot(rgb, float3(0.2126, 0.7152, 0.0722));
    rgb = mix(float3(luma), rgb, saturation);

    float warmth = clamp(temperature, -1.0, 1.0) * 0.15;
    rgb.r *= 1.0 + warmth;
    rgb.b *= 1.0 - warmth;

    rgb = pow(clamp(rgb, 0.0, 1.0), float3(1.0 / max(gammaValue, 0.01)));
    return half4(half3(rgb), source.a);
}
"""
