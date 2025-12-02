package com.misaka.hoshinoschedule.ui.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
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

object DataManagementSettingsScreen : Screen {
    @Composable
    override fun Content() {
        val ctx = LocalSettingsScreenContext.current
        val transferState = ctx.transferState

        Column(
            modifier = Modifier
                .padding(ctx.contentPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            SettingsSection(title = stringResource(R.string.settings_section_data)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = ctx.callbacks.onExport,
                        modifier = Modifier.weight(1f),
                        enabled = !transferState.isExporting && !transferState.isImporting
                    ) {
                        Text(
                            text = if (transferState.isExporting) {
                                stringResource(R.string.settings_exporting)
                            } else {
                                stringResource(R.string.settings_export)
                            }
                        )
                    }
                    OutlinedButton(
                        onClick = ctx.callbacks.onImport,
                        modifier = Modifier.weight(1f),
                        enabled = !transferState.isImporting && !transferState.isExporting
                    ) {
                        Text(
                            text = if (transferState.isImporting) {
                                stringResource(R.string.settings_importing)
                            } else {
                                stringResource(R.string.settings_import)
                            }
                        )
                    }
                }
                if (transferState.isImporting || transferState.isExporting) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterVertically))
                        Text(
                            text = if (transferState.isExporting) {
                                stringResource(R.string.settings_transfer_export_progress)
                            } else {
                                stringResource(R.string.settings_transfer_import_progress)
                            }
                        )
                    }
                }
            }
        }
    }
}
