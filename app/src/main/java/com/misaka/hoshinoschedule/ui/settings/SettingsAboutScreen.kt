package com.misaka.hoshinoschedule.ui.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsAboutScreen(
    viewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    SettingsScreen(
        state = uiState,
        page = com.misaka.hoshinoschedule.ui.settings.SettingsPage.About,
        snackbarHostState = snackbarHostState,
        onNavigate = { /* No navigation from about screen */ },
        onBack = onBack,
        onTimetableNameChange = viewModel::setTimetableName,
        onBackgroundColorSelected = viewModel::setBackgroundColor,
        onBackgroundImageSelect = { /* Not used in about screen */ },
        onClearBackgroundImage = { /* Not used in about screen */ },
        onVisibleFieldsChange = viewModel::setVisibleFields,
        onReminderLeadChange = viewModel::setReminderLead,
        onDndConfigChange = viewModel::setDndConfig,
        onTermStartDateChange = viewModel::setTermStartDate,
        onTotalWeeksChange = viewModel::setTotalWeeks,
        onShowNonCurrentWeekCoursesChange = viewModel::setShowNonCurrentWeekCourses,
        onWeekendVisibilityChange = viewModel::setWeekendVisibility,
        onHideEmptyWeekendChange = viewModel::setHideEmptyWeekend,
        onRequestDndAccess = { /* Not used in about screen */ },
        onAddPeriod = viewModel::addPeriod,
        onUpdatePeriod = viewModel::updatePeriod,
        onRemovePeriod = viewModel::removePeriod,
        onExport = { /* Not used in about screen */ },
        onImport = { /* Not used in about screen */ },
        onDeveloperModeChange = viewModel::setDeveloperMode,
        onDeveloperNotificationDelayChange = viewModel::setDeveloperTestNotificationDelay,
        onDeveloperDndDurationChange = viewModel::setDeveloperTestDndDuration,
        onDeveloperAutoDisableDndChange = viewModel::setDeveloperAutoDisableDnd,
        onDeveloperDndGapChange = viewModel::setDeveloperTestDndGap,
        onDeveloperDndSkipThresholdChange = viewModel::setDeveloperTestDndSkipThreshold,
        onTriggerTestNotification = { /* Not used in about screen */ },
        onTriggerTestDnd = { /* Not used in about screen */ },
        onTriggerTestDndConsecutive = { /* Not used in about screen */ },
        notificationsEnabled = true, // Not relevant for about screen
        onOpenNotificationSettings = { /* Not used in about screen */ },
        onOpenAboutLink = { url ->
            runCatching {
                context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, url.toUri()))
            }
        }
    )
}