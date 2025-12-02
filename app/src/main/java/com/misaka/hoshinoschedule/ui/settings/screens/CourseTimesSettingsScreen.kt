package com.misaka.hoshinoschedule.ui.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.ui.settings.LocalSettingsScreenContext
import com.misaka.hoshinoschedule.ui.settings.SettingsSection
import com.misaka.hoshinoschedule.ui.settings.components.PeriodEditorRow

object CourseTimesSettingsScreen : Screen {
    @Composable
    override fun Content() {
        val ctx = LocalSettingsScreenContext.current
        val periods = ctx.state.periods

        Column(
            modifier = Modifier
                .padding(ctx.contentPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsSection(title = stringResource(R.string.settings_section_periods)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    periods.sortedBy { it.sequence }.forEach { period ->
                        PeriodEditorRow(
                            period = period,
                            onUpdate = ctx.callbacks.onUpdatePeriod,
                            onRemove = ctx.callbacks.onRemovePeriod
                        )
                    }
                    FilledTonalButton(
                        onClick = ctx.callbacks.onAddPeriod,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.settings_add_period))
                    }
                }
            }
        }
    }
}
