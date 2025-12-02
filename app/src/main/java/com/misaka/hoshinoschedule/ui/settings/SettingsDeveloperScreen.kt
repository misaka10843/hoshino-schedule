package com.misaka.hoshinoschedule.ui.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misaka.hoshinoschedule.R
import kotlinx.coroutines.launch

@Composable
fun SettingsDeveloperScreen(
    viewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    SettingsScreen(
        state = uiState,
        page = com.misaka.hoshinoschedule.ui.settings.SettingsPage.Developer,
        snackbarHostState = snackbarHostState,
        onNavigate = { /* No navigation from developer screen */ },
        onBack = onBack,
        onTimetableNameChange = viewModel::setTimetableName,
        onBackgroundColorSelected = viewModel::setBackgroundColor,
        onBackgroundImageSelect = { /* Not used in developer screen */ },
        onClearBackgroundImage = { /* Not used in developer screen */ },
        onVisibleFieldsChange = viewModel::setVisibleFields,
        onReminderLeadChange = viewModel::setReminderLead,
        onDndConfigChange = viewModel::setDndConfig,
        onTermStartDateChange = viewModel::setTermStartDate,
        onTotalWeeksChange = viewModel::setTotalWeeks,
        onShowNonCurrentWeekCoursesChange = viewModel::setShowNonCurrentWeekCourses,
        onWeekendVisibilityChange = viewModel::setWeekendVisibility,
        onHideEmptyWeekendChange = viewModel::setHideEmptyWeekend,
        onRequestDndAccess = { /* Not used in developer screen */ },
        onAddPeriod = viewModel::addPeriod,
        onUpdatePeriod = viewModel::updatePeriod,
        onRemovePeriod = viewModel::removePeriod,
        onExport = { /* Not used in developer screen */ },
        onImport = { /* Not used in developer screen */ },
        onDeveloperModeChange = viewModel::setDeveloperMode,
        onDeveloperNotificationDelayChange = viewModel::setDeveloperTestNotificationDelay,
        onDeveloperDndDurationChange = viewModel::setDeveloperTestDndDuration,
        onDeveloperAutoDisableDndChange = viewModel::setDeveloperAutoDisableDnd,
        onDeveloperDndGapChange = viewModel::setDeveloperTestDndGap,
        onDeveloperDndSkipThresholdChange = viewModel::setDeveloperTestDndSkipThreshold,
        onTriggerTestNotification = {
            viewModel.triggerTestNotification()
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.settings_developer_test_notification_scheduled))
            }
        },
        onTriggerTestDnd = {
            viewModel.triggerTestDnd()
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.settings_developer_test_dnd_scheduled))
            }
        },
        onTriggerTestDndConsecutive = {
            viewModel.triggerTestDndConsecutive()
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.settings_developer_test_dnd_consecutive_scheduled))
            }
        },
        notificationsEnabled = true, // Not relevant for developer screen
        onOpenNotificationSettings = { /* Not used in developer screen */ },
        onOpenAboutLink = { /* Not used in developer screen */ }
    )
}