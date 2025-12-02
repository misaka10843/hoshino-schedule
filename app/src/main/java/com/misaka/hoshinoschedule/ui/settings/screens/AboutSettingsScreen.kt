package com.misaka.hoshinoschedule.ui.settings.screens

import android.os.Build
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.ui.settings.LocalSettingsScreenContext
import com.misaka.hoshinoschedule.ui.settings.SettingsCard

object AboutSettingsScreen : Screen {
    @Composable
    override fun Content() {
        val ctx = LocalSettingsScreenContext.current
        val context = LocalContext.current
        val versionName = remember {
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    ).versionName
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
                }
            }.getOrNull() ?: "1.0"
        }

        Column(
            modifier = Modifier
                .padding(ctx.contentPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsCard(title = stringResource(R.string.settings_about_app_section)) {
                Text(text = stringResource(R.string.settings_about_version, versionName))
                Text(text = stringResource(R.string.settings_about_author))
            }
            SettingsCard(title = stringResource(R.string.settings_about_links)) {
                TextButton(onClick = { ctx.callbacks.onOpenAboutLink(context.getString(R.string.settings_about_repo_url)) }) {
                    Text(stringResource(R.string.settings_about_open_repo))
                }
                TextButton(onClick = { ctx.callbacks.onOpenAboutLink(context.getString(R.string.settings_about_license_url)) }) {
                    Text(stringResource(R.string.settings_about_view_license))
                }
            }
        }
    }
}
