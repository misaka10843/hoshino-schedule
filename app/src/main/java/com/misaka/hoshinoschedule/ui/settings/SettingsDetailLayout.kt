package com.misaka.hoshinoschedule.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.misaka.hoshinoschedule.R

@Composable
fun SettingsDetailScaffold(
    titleRes: Int,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    content: @Composable Column.() -> Unit
) {
    val ctx = LocalSettingsScreenContext.current
    val detailMode = LocalSettingsDetailMode.current
    val navigator = LocalNavigator.currentOrThrow

    if (detailMode == SettingsDetailMode.SINGLE_PANE) {
        Scaffold(
            topBar = {
                SettingsTopAppBar(
                    title = stringResource(titleRes),
                    showNavigation = true,
                    onNavigationClick = {
                        if (!navigator.pop()) {
                            ctx.closeSettings()
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = ctx.snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ctx.contentPadding)
                    .padding(innerPadding)
                    .padding(contentPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                content = content
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            content = content
        )
    }
}
