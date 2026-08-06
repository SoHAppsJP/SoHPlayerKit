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

package jp.sohapps.sohplayerkit.ui.controls

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Scrollable player-control panel shared by player hosts.
 *
 * The host supplies app-specific top actions and optional supplemental rows, such as DVD
 * navigation. Transport and settings controls can then be placed in [content] without the
 * shared UI depending on a playback engine or host application resources.
 */
@Composable
fun PlayerControlPanel(
    modifier: Modifier = Modifier,
    topActions: @Composable RowScope.() -> Unit,
    supplementalContent: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.68f), RoundedCornerShape(8.dp))
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            content = topActions
        )

        supplementalContent()
        content()
    }
}

/**
 * Icon button used by host-provided player-panel actions.
 *
 * Supplying resources and click behavior from the host keeps this component independent of
 * application-specific actions such as opening a list, switching engines, or DVD navigation.
 */
@Composable
fun PlayerPanelActionButton(
    @DrawableRes iconRes: Int,
    label: String? = null,
    value: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 1.dp, vertical = 2.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label ?: value,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (!value.isNullOrBlank()) {
            Text(
                text = value,
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
