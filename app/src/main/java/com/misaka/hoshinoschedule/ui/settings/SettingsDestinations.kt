package com.misaka.hoshinoschedule.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.graphics.vector.ImageVector
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.ui.settings.screens.AboutSettingsScreen
import com.misaka.hoshinoschedule.ui.settings.screens.AppearanceSettingsScreen
import com.misaka.hoshinoschedule.ui.settings.screens.BasicSettingsScreen
import com.misaka.hoshinoschedule.ui.settings.screens.CourseTimesSettingsScreen
import com.misaka.hoshinoschedule.ui.settings.screens.DataManagementSettingsScreen
import com.misaka.hoshinoschedule.ui.settings.screens.DeveloperSettingsScreen
import com.misaka.hoshinoschedule.ui.settings.screens.DisplayFieldsSettingsScreen
import com.misaka.hoshinoschedule.ui.settings.screens.DoNotDisturbSettingsScreen
import com.misaka.hoshinoschedule.ui.settings.screens.NotificationsSettingsScreen
import cafe.adriel.voyager.core.screen.Screen

enum class SettingsDestination(
    val icon: ImageVector,
    val titleRes: Int,
    val summaryRes: Int,
    val isCategory: Boolean = true,
    val screenFactory: () -> Screen
) {
    Appearance(
        icon = Icons.Default.Palette,
        titleRes = R.string.settings_category_appearance_title,
        summaryRes = R.string.settings_category_appearance_summary,
        screenFactory = { AppearanceSettingsScreen }
    ),
    Basic(
        icon = Icons.Default.Settings,
        titleRes = R.string.settings_category_basic_title,
        summaryRes = R.string.settings_category_basic_summary,
        screenFactory = { BasicSettingsScreen }
    ),
    DisplayFields(
        icon = Icons.AutoMirrored.Filled.ViewList,
        titleRes = R.string.settings_category_display_fields_title,
        summaryRes = R.string.settings_category_display_fields_summary,
        screenFactory = { DisplayFieldsSettingsScreen }
    ),
    Notifications(
        icon = Icons.Default.Notifications,
        titleRes = R.string.settings_category_notifications_title,
        summaryRes = R.string.settings_category_notifications_summary,
        screenFactory = { NotificationsSettingsScreen }
    ),
    DoNotDisturb(
        icon = Icons.Default.DoNotDisturb,
        titleRes = R.string.settings_category_dnd_title,
        summaryRes = R.string.settings_category_dnd_summary,
        screenFactory = { DoNotDisturbSettingsScreen }
    ),
    CourseTimes(
        icon = Icons.Default.Schedule,
        titleRes = R.string.settings_category_course_times_title,
        summaryRes = R.string.settings_category_course_times_summary,
        screenFactory = { CourseTimesSettingsScreen }
    ),
    DataManagement(
        icon = Icons.Default.Storage,
        titleRes = R.string.settings_category_data_title,
        summaryRes = R.string.settings_category_data_summary,
        screenFactory = { DataManagementSettingsScreen }
    ),
    Developer(
        icon = Icons.Default.Build,
        titleRes = R.string.settings_developer_entry_title,
        summaryRes = R.string.settings_developer_entry_subtitle,
        isCategory = false,
        screenFactory = { DeveloperSettingsScreen }
    ),
    About(
        icon = Icons.Default.Info,
        titleRes = R.string.settings_about_title,
        summaryRes = R.string.settings_about_entry_subtitle,
        isCategory = false,
        screenFactory = { AboutSettingsScreen }
    );
    
    companion object {
        fun categories() = entries.filter { it.isCategory }
        fun additional() = entries.filterNot { it.isCategory }
    }
}
