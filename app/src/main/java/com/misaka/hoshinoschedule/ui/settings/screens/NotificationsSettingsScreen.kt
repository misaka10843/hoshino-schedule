package com.misaka.hoshinoschedule.ui.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.ui.settings.LocalSettingsScreenContext
import com.misaka.hoshinoschedule.ui.settings.MaxReminderLeadMinutes
import com.misaka.hoshinoschedule.ui.settings.SettingsSection
import com.misaka.hoshinoschedule.ui.settings.SettingsSliderContainer
import com.misaka.hoshinoschedule.ui.settings.SliderWithValue

object NotificationsSettingsScreen : Screen {
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
            SettingsSection(title = stringResource(R.string.settings_section_notifications)) {
                SettingsSliderContainer {
                    SliderWithValue(
                        label = stringResource(R.string.settings_reminder_lead_label_short),
                        value = preferences.reminderLeadMinutes,
                        range = 0..MaxReminderLeadMinutes,
                        valueSuffix = stringResource(R.string.settings_slider_minutes_suffix),
                        onChange = ctx.callbacks.onReminderLeadChange
                    )
                }
            }
        }
    }
}
