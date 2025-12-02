package com.misaka.hoshinoschedule.ui.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.data.settings.BackgroundMode
import com.misaka.hoshinoschedule.data.settings.UserPreferences
import com.misaka.hoshinoschedule.ui.settings.ColorOption
import com.misaka.hoshinoschedule.ui.settings.LocalSettingsScreenContext
import com.misaka.hoshinoschedule.ui.settings.SettingsSection

object AppearanceSettingsScreen : Screen {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val ctx = LocalSettingsScreenContext.current
        val preferences = ctx.state.preferences
        val colorOptions = remember(preferences.backgroundValue) {
            (listOf(preferences.backgroundValue) + listOf(
                UserPreferences().backgroundValue,
                "#FFB300",
                "#FF7043",
                "#8BC34A",
                "#29B6F6",
                "#9C27B0"
            )).distinct()
        }
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .padding(ctx.contentPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            SettingsSection(title = stringResource(R.string.settings_section_background)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    colorOptions.forEach { hex ->
                        ColorOption(
                            colorHex = hex,
                            selected = preferences.backgroundMode == BackgroundMode.COLOR &&
                                preferences.backgroundValue == hex,
                            onClick = { ctx.callbacks.onBackgroundColorSelected(hex) }
                        )
                    }
                    TextButton(onClick = ctx.callbacks.onBackgroundImageSelect) {
                        Text(stringResource(R.string.settings_background_pick))
                    }
                    if (preferences.backgroundMode == BackgroundMode.IMAGE) {
                        TextButton(onClick = ctx.callbacks.onClearBackgroundImage) {
                            Text(stringResource(R.string.settings_background_use_color))
                        }
                    }
                }

                if (preferences.backgroundMode == BackgroundMode.IMAGE) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(preferences.backgroundValue)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
            }
        }
    }
}
