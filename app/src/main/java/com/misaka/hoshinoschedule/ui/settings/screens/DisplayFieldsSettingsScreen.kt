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
import com.misaka.hoshinoschedule.data.settings.CourseDisplayField
import com.misaka.hoshinoschedule.ui.settings.LocalSettingsScreenContext
import com.misaka.hoshinoschedule.ui.settings.SettingsCheckboxItem
import com.misaka.hoshinoschedule.ui.settings.SettingsSection

object DisplayFieldsSettingsScreen : Screen {
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
            SettingsSection(title = stringResource(R.string.settings_section_course_display)) {
                CourseDisplayField.entries.forEach { field ->
                    val checked = field in preferences.visibleFields
                    SettingsCheckboxItem(
                        title = fieldLabel(field),
                        checked = checked,
                        onCheckedChange = { isChecked ->
                            val updated = preferences.visibleFields.toMutableSet()
                            if (isChecked) {
                                updated.add(field)
                            } else {
                                updated.remove(field)
                            }
                            if (updated.isEmpty()) {
                                updated.add(CourseDisplayField.NAME)
                            }
                            ctx.callbacks.onVisibleFieldsChange(updated)
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun fieldLabel(field: CourseDisplayField): String = when (field) {
        CourseDisplayField.NAME -> stringResource(R.string.settings_field_name)
        CourseDisplayField.TEACHER -> stringResource(R.string.settings_field_teacher)
        CourseDisplayField.LOCATION -> stringResource(R.string.settings_field_location)
        CourseDisplayField.NOTES -> stringResource(R.string.settings_field_notes)
    }
}
