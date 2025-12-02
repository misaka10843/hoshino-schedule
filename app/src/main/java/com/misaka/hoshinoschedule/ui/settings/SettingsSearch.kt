package com.misaka.hoshinoschedule.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.misaka.hoshinoschedule.R

@Immutable
data class SettingsSearchItem(
    val id: String,
    val destination: SettingsDestination,
    val title: String,
    val subtitle: String,
    val keywords: List<String>
) {
    fun matches(query: String): Boolean {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) return false
        return title.lowercase().contains(normalized) ||
            subtitle.lowercase().contains(normalized) ||
            keywords.any { it.lowercase().contains(normalized) }
    }
}

class SettingsSearchState internal constructor(
    private val defaultItems: List<SettingsSearchItem>,
    private val searchableItems: List<SettingsSearchItem>
) {
    var query by mutableStateOf("")
        private set

    val results: List<SettingsSearchItem>
        get() = if (query.isBlank()) {
            defaultItems
        } else {
            searchableItems.filter { it.matches(query) }
        }

    fun updateQuery(value: String) {
        query = value
    }

    fun clear() {
        query = ""
    }
}

@Composable
fun rememberSettingsSearchState(): SettingsSearchState {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    val defaultItems = remember(configuration) {
        SettingsDestination.entries.map { destination ->
            SettingsSearchItem(
                id = "dest_${destination.name}",
                destination = destination,
                title = context.getString(destination.titleRes),
                subtitle = context.getString(destination.summaryRes),
                keywords = defaultKeywordsFor(destination)
            )
        }
    }

    val extraItems = remember(configuration) {
        listOf(
            SettingsSearchItem(
                id = "extra_term_start",
                destination = SettingsDestination.Basic,
                title = context.getString(R.string.settings_term_start_label),
                subtitle = context.getString(R.string.settings_section_timetable),
                keywords = listOf("term", "start date", "semester", "reset")
            ),
            SettingsSearchItem(
                id = "extra_background",
                destination = SettingsDestination.Appearance,
                title = context.getString(R.string.settings_section_background),
                subtitle = context.getString(R.string.settings_category_appearance_title),
                keywords = listOf("background", "image", "photo", "wallpaper", "color")
            ),
            SettingsSearchItem(
                id = "extra_teacher_field",
                destination = SettingsDestination.DisplayFields,
                title = context.getString(R.string.settings_field_teacher),
                subtitle = context.getString(R.string.settings_section_course_display),
                keywords = listOf("teacher", "professor", "lecturer", "instructor")
            ),
            SettingsSearchItem(
                id = "extra_backup",
                destination = SettingsDestination.DataManagement,
                title = context.getString(R.string.settings_export),
                subtitle = context.getString(R.string.settings_section_data),
                keywords = listOf("backup", "export", "save", "json")
            ),
            SettingsSearchItem(
                id = "extra_restore",
                destination = SettingsDestination.DataManagement,
                title = context.getString(R.string.settings_import),
                subtitle = context.getString(R.string.settings_section_data),
                keywords = listOf("restore", "import", "load", "json")
            ),
            SettingsSearchItem(
                id = "extra_dnd",
                destination = SettingsDestination.DoNotDisturb,
                title = context.getString(R.string.settings_section_dnd),
                subtitle = context.getString(R.string.settings_category_dnd_summary),
                keywords = listOf("dnd", "silence", "mute", "do not disturb")
            ),
            SettingsSearchItem(
                id = "extra_notifications_test",
                destination = SettingsDestination.Developer,
                title = context.getString(R.string.settings_developer_send_test_notification),
                subtitle = context.getString(R.string.settings_developer_testing_section),
                keywords = listOf("test", "notification", "developer")
            )
        )
    }

    return remember(defaultItems, extraItems) {
        SettingsSearchState(
            defaultItems = defaultItems,
            searchableItems = defaultItems + extraItems
        )
    }
}

private fun defaultKeywordsFor(destination: SettingsDestination): List<String> = when (destination) {
    SettingsDestination.Appearance -> listOf("appearance", "background", "color", "image", "theme")
    SettingsDestination.Basic -> listOf("basic", "term", "weeks", "weekend", "timetable")
    SettingsDestination.DisplayFields -> listOf("fields", "display", "teacher", "location", "notes")
    SettingsDestination.Notifications -> listOf("notification", "reminder", "lead")
    SettingsDestination.DoNotDisturb -> listOf("dnd", "silence", "auto", "mute")
    SettingsDestination.CourseTimes -> listOf("period", "time", "schedule", "hours")
    SettingsDestination.DataManagement -> listOf("data", "backup", "import", "export")
    SettingsDestination.Developer -> listOf("developer", "debug", "testing")
    SettingsDestination.About -> listOf("about", "version", "license")
}
