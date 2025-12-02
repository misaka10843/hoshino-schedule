package com.misaka.hoshinoschedule.ui.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.ui.settings.LocalSettingsScreenContext
import com.misaka.hoshinoschedule.ui.settings.MaxTotalWeeks
import com.misaka.hoshinoschedule.ui.settings.SettingsClickableItem
import com.misaka.hoshinoschedule.ui.settings.SettingsSection
import com.misaka.hoshinoschedule.ui.settings.SettingsToggleItem
import com.misaka.hoshinoschedule.util.termStartDate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object BasicSettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val ctx = LocalSettingsScreenContext.current
        val preferences = ctx.state.preferences
        val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy/MM/dd") }
        val zoneId = remember { ZoneId.systemDefault() }
        var nameDialogVisible by remember { mutableStateOf(false) }
        var weeksDialogVisible by remember { mutableStateOf(false) }
        var datePickerVisible by remember { mutableStateOf(false) }
        var timetableNameDraft by rememberSaveable(preferences.timetableName) {
            mutableStateOf(preferences.timetableName)
        }
        var totalWeeksDraft by rememberSaveable(preferences.totalWeeks) {
            mutableStateOf(preferences.totalWeeks.toString())
        }

        val weeksValue = totalWeeksDraft.toIntOrNull()
        val weeksValid = weeksValue != null && weeksValue in 1..MaxTotalWeeks
        val hasPendingChanges =
            (timetableNameDraft != preferences.timetableName) ||
                (weeksValid && weeksValue != preferences.totalWeeks)

        Column(
            modifier = Modifier
                .padding(ctx.contentPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            SettingsSection(title = stringResource(R.string.settings_section_timetable)) {
                SettingsClickableItem(
                    title = stringResource(R.string.settings_timetable_name_label),
                    summary = timetableNameDraft.ifBlank {
                        stringResource(R.string.settings_value_not_set)
                    },
                    onClick = { nameDialogVisible = true }
                )

                val termStartDate = preferences.termStartDate()
                SettingsClickableItem(
                    title = stringResource(R.string.settings_term_start_label),
                    summary = termStartDate?.format(dateFormatter)
                        ?: stringResource(R.string.settings_value_not_set),
                    onClick = { datePickerVisible = true },
                    trailingContent = {
                        if (termStartDate != null) {
                            TextButton(onClick = { ctx.callbacks.onTermStartDateChange(null) }) {
                                Text(stringResource(R.string.settings_term_start_clear_action))
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

                SettingsClickableItem(
                    title = stringResource(R.string.settings_total_weeks_label),
                    summary = stringResource(
                        R.string.settings_total_weeks_summary,
                        weeksValue ?: preferences.totalWeeks
                    ),
                    onClick = { weeksDialogVisible = true }
                )

                SettingsToggleItem(
                    title = stringResource(R.string.settings_show_non_current_week_title),
                    summary = stringResource(R.string.settings_show_non_current_week_hint),
                    checked = preferences.showNonCurrentWeekCourses,
                    onCheckedChange = ctx.callbacks.onShowNonCurrentWeekCoursesChange
                )
            }

            SettingsSection(title = stringResource(R.string.settings_section_weekend)) {
                SettingsToggleItem(
                    title = stringResource(R.string.settings_show_saturday),
                    checked = preferences.showSaturday,
                    onCheckedChange = { ctx.callbacks.onWeekendVisibilityChange(it, preferences.showSunday) }
                )
                SettingsToggleItem(
                    title = stringResource(R.string.settings_show_sunday),
                    checked = preferences.showSunday,
                    onCheckedChange = { ctx.callbacks.onWeekendVisibilityChange(preferences.showSaturday, it) }
                )
                SettingsToggleItem(
                    title = stringResource(R.string.settings_hide_empty_weekend),
                    summary = stringResource(R.string.settings_hide_empty_weekend_hint),
                    checked = preferences.hideEmptyWeekends,
                    onCheckedChange = ctx.callbacks.onHideEmptyWeekendChange
                )
            }

            Button(
                onClick = {
                    ctx.callbacks.onTimetableNameChange(timetableNameDraft.trim())
                    weeksValue?.let(ctx.callbacks.onTotalWeeksChange)
                },
                enabled = hasPendingChanges && weeksValid,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.settings_basic_save_changes))
            }
        }

        if (nameDialogVisible) {
            var draft by remember { mutableStateOf(timetableNameDraft) }
            AlertDialog(
                onDismissRequest = { nameDialogVisible = false },
                confirmButton = {
                    TextButton(onClick = {
                        timetableNameDraft = draft
                        nameDialogVisible = false
                    }) {
                        Text(stringResource(R.string.common_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { nameDialogVisible = false }) {
                        Text(stringResource(R.string.common_cancel))
                    }
                },
                title = { Text(stringResource(R.string.settings_basic_name_dialog_title)) },
                text = {
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }

        if (weeksDialogVisible) {
            var draft by remember { mutableStateOf(totalWeeksDraft) }
            val isValid = draft.toIntOrNull()?.let { it in 1..MaxTotalWeeks } == true
            AlertDialog(
                onDismissRequest = { weeksDialogVisible = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            totalWeeksDraft = draft
                            weeksDialogVisible = false
                        },
                        enabled = isValid
                    ) {
                        Text(stringResource(R.string.common_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { weeksDialogVisible = false }) {
                        Text(stringResource(R.string.common_cancel))
                    }
                },
                title = { Text(stringResource(R.string.settings_basic_total_weeks_dialog_title)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = draft,
                            onValueChange = { input ->
                                draft = input.filter { it.isDigit() }.take(2)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = if (isValid || draft.isBlank()) {
                                stringResource(R.string.settings_basic_total_weeks_dialog_hint)
                            } else {
                                stringResource(R.string.settings_basic_total_weeks_invalid)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isValid || draft.isBlank()) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
            )
        }

        if (datePickerVisible) {
            val state = rememberDatePickerState(
                initialSelectedDateMillis = preferences.termStartDate()?.atStartOfDay(zoneId)?.toInstant()
                    ?.toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { datePickerVisible = false },
                confirmButton = {
                    TextButton(onClick = {
                        val selected = state.selectedDateMillis
                        val date = selected?.let {
                            Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate()
                        }
                        ctx.callbacks.onTermStartDateChange(date)
                        datePickerVisible = false
                    }) {
                        Text(stringResource(R.string.common_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { datePickerVisible = false }) {
                        Text(stringResource(R.string.common_cancel))
                    }
                }
            ) {
                DatePicker(state = state)
            }
        }
    }
}
