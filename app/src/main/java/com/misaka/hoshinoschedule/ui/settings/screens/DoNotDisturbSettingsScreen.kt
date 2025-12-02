package com.misaka.hoshinoschedule.ui.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.ui.settings.LocalSettingsScreenContext
import com.misaka.hoshinoschedule.ui.settings.MaxDndLeadMinutes
import com.misaka.hoshinoschedule.ui.settings.MaxDndReleaseMinutes
import com.misaka.hoshinoschedule.ui.settings.MaxDndSkipThresholdMinutes
import com.misaka.hoshinoschedule.ui.settings.SettingsSection
import com.misaka.hoshinoschedule.ui.settings.SettingsSliderContainer
import com.misaka.hoshinoschedule.ui.settings.SettingsToggleItem
import com.misaka.hoshinoschedule.ui.settings.SliderWithValue

object DoNotDisturbSettingsScreen : Screen {
    @Composable
    override fun Content() {
        val ctx = LocalSettingsScreenContext.current
        val preferences = ctx.state.preferences

        Column(
            modifier = Modifier
                .padding(ctx.contentPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            SettingsSection(title = stringResource(R.string.settings_section_dnd)) {
                val enabled = preferences.dndEnabled
                SettingsToggleItem(
                    title = stringResource(if (enabled) R.string.settings_dnd_enabled else R.string.settings_dnd_disabled),
                    checked = enabled,
                    onCheckedChange = { isEnabled ->
                        ctx.callbacks.onDndConfigChange(
                            isEnabled,
                            preferences.dndLeadMinutes,
                            preferences.dndReleaseMinutes,
                            preferences.dndSkipBreakThresholdMinutes
                        )
                    }
                )

                if (enabled) {
                    SettingsSliderContainer {
                        SliderWithValue(
                            label = stringResource(R.string.settings_dnd_enable_before),
                            value = preferences.dndLeadMinutes,
                            range = 0..MaxDndLeadMinutes,
                            valueSuffix = stringResource(R.string.settings_slider_minutes_suffix),
                            onChange = { lead ->
                                ctx.callbacks.onDndConfigChange(
                                    true,
                                    lead,
                                    preferences.dndReleaseMinutes,
                                    preferences.dndSkipBreakThresholdMinutes
                                )
                            }
                        )
                    }
                    SettingsSliderContainer {
                        SliderWithValue(
                            label = stringResource(R.string.settings_dnd_disable_after),
                            value = preferences.dndReleaseMinutes,
                            range = 0..MaxDndReleaseMinutes,
                            valueSuffix = stringResource(R.string.settings_slider_minutes_suffix),
                            onChange = { release ->
                                ctx.callbacks.onDndConfigChange(
                                    true,
                                    preferences.dndLeadMinutes,
                                    release,
                                    preferences.dndSkipBreakThresholdMinutes
                                )
                            }
                        )
                    }
                    SettingsSliderContainer {
                        SliderWithValue(
                            label = stringResource(R.string.settings_dnd_keep_enabled),
                            value = preferences.dndSkipBreakThresholdMinutes,
                            range = 0..MaxDndSkipThresholdMinutes,
                            valueSuffix = stringResource(R.string.settings_slider_minutes_suffix),
                            onChange = { threshold ->
                                ctx.callbacks.onDndConfigChange(
                                    true,
                                    preferences.dndLeadMinutes,
                                    preferences.dndReleaseMinutes,
                                    threshold
                                )
                            }
                        )
                    }
                    TextButton(onClick = ctx.callbacks.onRequestDndAccess) {
                        Text(stringResource(R.string.settings_dnd_grant_access))
                    }
                }
            }
        }
    }
}
