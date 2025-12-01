package com.misaka.hoshinoschedule.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misaka.hoshinoschedule.R
import kotlinx.coroutines.launch

@Composable
fun SettingsCategoryScreen(
    viewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    category: SettingsCategory,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.export(uri) { success ->
                scope.launch {
                    snackbarHostState.showSnackbar(
                        context.getString(if (success) R.string.settings_export_success else R.string.settings_export_failure)
                    )
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.import(uri) { success ->
                scope.launch {
                    snackbarHostState.showSnackbar(
                        context.getString(if (success) R.string.settings_import_success else R.string.settings_import_failure)
                    )
                }
            }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setBackgroundImage(it.toString()) }
    }

    SettingsScreen(
        state = uiState,
        page = com.misaka.hoshinoschedule.ui.settings.SettingsPage.Category(category),
        snackbarHostState = snackbarHostState,
        onNavigate = { /* No navigation from category screens */ },
        onBack = onBack,
        onTimetableNameChange = viewModel::setTimetableName,
        onBackgroundColorSelected = viewModel::setBackgroundColor,
        onBackgroundImageSelect = { imagePicker.launch("image/*") },
        onClearBackgroundImage = { 
            viewModel.setBackgroundColor(com.misaka.hoshinoschedule.data.settings.UserPreferences().backgroundValue) 
        },
        onVisibleFieldsChange = viewModel::setVisibleFields,
        onReminderLeadChange = viewModel::setReminderLead,
        onDndConfigChange = viewModel::setDndConfig,
        onTermStartDateChange = viewModel::setTermStartDate,
        onTotalWeeksChange = viewModel::setTotalWeeks,
        onShowNonCurrentWeekCoursesChange = viewModel::setShowNonCurrentWeekCourses,
        onWeekendVisibilityChange = viewModel::setWeekendVisibility,
        onHideEmptyWeekendChange = viewModel::setHideEmptyWeekend,
        onRequestDndAccess = {
            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        },
        onAddPeriod = viewModel::addPeriod,
        onUpdatePeriod = viewModel::updatePeriod,
        onRemovePeriod = viewModel::removePeriod,
        onExport = { exportLauncher.launch("kirara_schedule.json") },
        onImport = { importLauncher.launch(arrayOf("application/json")) },
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
        notificationsEnabled = notificationsEnabled,
        onOpenNotificationSettings = {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            context.startActivity(intent)
        },
        onOpenAboutLink = { url ->
            runCatching {
                context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
            }
        }
    )
}