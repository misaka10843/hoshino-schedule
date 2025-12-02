package com.misaka.hoshinoschedule.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.data.settings.UserPreferences
import kotlinx.coroutines.launch

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
    var transferState by remember { mutableStateOf(SettingsTransferState()) }

    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
            if (uri != null) {
                transferState = transferState.copy(isExporting = true)
                viewModel.export(uri) { success ->
                    transferState = transferState.copy(isExporting = false)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            context.getString(if (success) R.string.settings_export_success else R.string.settings_export_failure)
                        )
                    }
                }
            }
        }

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                transferState = transferState.copy(isImporting = true)
                viewModel.import(uri) { success ->
                    transferState = transferState.copy(isImporting = false)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            context.getString(if (success) R.string.settings_import_success else R.string.settings_import_failure)
                        )
                    }
                }
            }
        }

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.setBackgroundImage(it.toString()) }
        }

    val callbacks = SettingsCallbacks(
        onTimetableNameChange = viewModel::setTimetableName,
        onBackgroundColorSelected = viewModel::setBackgroundColor,
        onBackgroundImageSelect = { imagePicker.launch("image/*") },
        onClearBackgroundImage = { viewModel.setBackgroundColor(UserPreferences().backgroundValue) },
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

    Navigator(SettingsHomeScreen) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            CompositionLocalProvider(
                LocalSettingsScreenContext provides SettingsScreenContext(
                    state = uiState,
                    callbacks = callbacks,
                    snackbarHostState = snackbarHostState,
                    transferState = transferState,
                    notificationsEnabled = notificationsEnabled,
                    contentPadding = paddingValues,
                    closeSettings = onBack
                ),
                LocalSettingsDetailMode provides SettingsDetailMode.SINGLE_PANE
            ) {
                cafe.adriel.voyager.navigator.CurrentScreen()
            }
        }
    }
}
