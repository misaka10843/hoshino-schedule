package com.misaka.hoshinoschedule.ui.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.misaka.hoshinoschedule.data.model.PeriodDefinition
import com.misaka.hoshinoschedule.data.settings.CourseDisplayField
import java.time.LocalDate

@Immutable
data class SettingsTransferState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false
)

@Immutable
data class SettingsCallbacks(
    val onTimetableNameChange: (String) -> Unit,
    val onBackgroundColorSelected: (String) -> Unit,
    val onBackgroundImageSelect: () -> Unit,
    val onClearBackgroundImage: () -> Unit,
    val onVisibleFieldsChange: (Set<CourseDisplayField>) -> Unit,
    val onReminderLeadChange: (Int) -> Unit,
    val onDndConfigChange: (Boolean, Int, Int, Int) -> Unit,
    val onTermStartDateChange: (LocalDate?) -> Unit,
    val onTotalWeeksChange: (Int) -> Unit,
    val onShowNonCurrentWeekCoursesChange: (Boolean) -> Unit,
    val onWeekendVisibilityChange: (Boolean, Boolean) -> Unit,
    val onHideEmptyWeekendChange: (Boolean) -> Unit,
    val onRequestDndAccess: () -> Unit,
    val onAddPeriod: () -> Unit,
    val onUpdatePeriod: (PeriodEditInput) -> Unit,
    val onRemovePeriod: (Int) -> Unit,
    val onExport: () -> Unit,
    val onImport: () -> Unit,
    val onDeveloperModeChange: (Boolean) -> Unit,
    val onDeveloperNotificationDelayChange: (Int) -> Unit,
    val onDeveloperDndDurationChange: (Int) -> Unit,
    val onDeveloperAutoDisableDndChange: (Boolean) -> Unit,
    val onDeveloperDndGapChange: (Int) -> Unit,
    val onDeveloperDndSkipThresholdChange: (Int) -> Unit,
    val onTriggerTestNotification: () -> Unit,
    val onTriggerTestDnd: () -> Unit,
    val onTriggerTestDndConsecutive: () -> Unit,
    val onOpenNotificationSettings: () -> Unit,
    val onOpenAboutLink: (String) -> Unit
)

@Immutable
data class SettingsScreenContext(
    val state: SettingsUiState,
    val callbacks: SettingsCallbacks,
    val snackbarHostState: SnackbarHostState,
    val transferState: SettingsTransferState,
    val notificationsEnabled: Boolean,
    val contentPadding: PaddingValues,
    val closeSettings: () -> Unit
)

val LocalSettingsScreenContext = staticCompositionLocalOf<SettingsScreenContext> {
    error("SettingsScreenContext not provided")
}

enum class SettingsDetailMode { SINGLE_PANE, TWO_PANE }

val LocalSettingsDetailMode = staticCompositionLocalOf { SettingsDetailMode.SINGLE_PANE }
