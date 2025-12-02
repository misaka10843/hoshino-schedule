package com.misaka.hoshinoschedule.ui.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.ui.settings.LocalSettingsScreenContext
import com.misaka.hoshinoschedule.ui.settings.MaxDeveloperTestDndGapMinutes
import com.misaka.hoshinoschedule.ui.settings.MaxDeveloperTestDndSkipMinutes
import com.misaka.hoshinoschedule.ui.settings.MaxTestDndDurationMinutes
import com.misaka.hoshinoschedule.ui.settings.MaxTestNotificationDelaySeconds
import com.misaka.hoshinoschedule.ui.settings.SettingsCard
import com.misaka.hoshinoschedule.ui.settings.SliderWithValue
import com.misaka.hoshinoschedule.ui.settings.SwitchRow

object DeveloperSettingsScreen : Screen {
    @Composable
    override fun Content() {
        val ctx = LocalSettingsScreenContext.current
        val preferences = ctx.state.preferences

        Column(
            modifier = Modifier
                .padding(ctx.contentPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsCard(title = stringResource(R.string.settings_developer_mode_section)) {
                SwitchRow(
                    title = stringResource(R.string.settings_developer_mode_enable),
                    checked = preferences.developerModeEnabled,
                    onCheckedChange = ctx.callbacks.onDeveloperModeChange
                )
                if (!ctx.notificationsEnabled) {
                    Text(
                        text = stringResource(R.string.settings_developer_notifications_disabled),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(onClick = ctx.callbacks.onOpenNotificationSettings) {
                        Text(stringResource(R.string.settings_developer_notifications_open_settings))
                    }
                }
            }

            if (preferences.developerModeEnabled) {
                SettingsCard(title = stringResource(R.string.settings_developer_testing_section)) {
                    SliderWithValue(
                        label = stringResource(R.string.settings_developer_test_notification_delay),
                        value = preferences.developerTestNotificationDelaySeconds,
                        range = 0..MaxTestNotificationDelaySeconds,
                        valueSuffix = stringResource(R.string.settings_slider_seconds_suffix),
                        onChange = ctx.callbacks.onDeveloperNotificationDelayChange
                    )
                    Button(onClick = ctx.callbacks.onTriggerTestNotification) {
                        Text(stringResource(R.string.settings_developer_send_test_notification))
                    }

                    SwitchRow(
                        title = stringResource(R.string.settings_developer_dnd_auto_disable),
                        checked = preferences.developerAutoDisableDnd,
                        onCheckedChange = ctx.callbacks.onDeveloperAutoDisableDndChange
                    )
                    SliderWithValue(
                        label = stringResource(R.string.settings_developer_test_dnd_duration),
                        value = preferences.developerTestDndDurationMinutes,
                        range = 1..MaxTestDndDurationMinutes,
                        valueSuffix = stringResource(R.string.settings_slider_minutes_suffix),
                        onChange = ctx.callbacks.onDeveloperDndDurationChange
                    )
                    SliderWithValue(
                        label = stringResource(R.string.settings_developer_test_dnd_gap),
                        value = preferences.developerTestDndGapMinutes,
                        range = 0..MaxDeveloperTestDndGapMinutes,
                        valueSuffix = stringResource(R.string.settings_slider_minutes_suffix),
                        onChange = ctx.callbacks.onDeveloperDndGapChange
                    )
                    SliderWithValue(
                        label = stringResource(R.string.settings_developer_test_dnd_skip_threshold),
                        value = preferences.developerTestDndSkipThresholdMinutes,
                        range = 0..MaxDeveloperTestDndSkipMinutes,
                        valueSuffix = stringResource(R.string.settings_slider_minutes_suffix),
                        onChange = ctx.callbacks.onDeveloperDndSkipThresholdChange
                    )
                    Button(onClick = ctx.callbacks.onTriggerTestDnd) {
                        Text(stringResource(R.string.settings_developer_toggle_dnd))
                    }
                    Button(onClick = ctx.callbacks.onTriggerTestDndConsecutive) {
                        Text(stringResource(R.string.settings_developer_toggle_dnd_consecutive))
                    }
                }
            }
        }
    }
}
