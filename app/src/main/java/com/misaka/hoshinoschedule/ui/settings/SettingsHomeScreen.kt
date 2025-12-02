package com.misaka.hoshinoschedule.ui.settings

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.runtime.CompositionLocalProvider
import com.misaka.hoshinoschedule.R

object SettingsHomeScreen : Screen {
    @SuppressLint("UnusedBoxWithConstraintsScope")
    @Composable
    override fun Content() {
        val ctx = LocalSettingsScreenContext.current
        val searchState = rememberSettingsSearchState()
        val focusManager = LocalFocusManager.current
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ctx.contentPadding)
        ) {
            SettingsTopAppBar(
                title = stringResource(R.string.settings_title),
                showNavigation = true,
                onNavigationClick = ctx.closeSettings
            )
            Divider()

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isTwoPane = maxWidth >= 900.dp

                if (isTwoPane) {
                    TwoPaneLayout(searchState)
                } else {
                    SinglePaneLayout(
                        searchState = searchState,
                        onDestinationSelected = { destination ->
                            focusManager.clearFocus()
                            navigator.push(destination.screenFactory())
                            searchState.clear()
                        }
                    )
                }
            }
        }
    }
}

/* --------------------------------------------------------- */
/* Single Pane 修复版（已解决 scroll 冲突）                 */
/* --------------------------------------------------------- */

@Composable
private fun SinglePaneLayout(
    searchState: SettingsSearchState,
    onDestinationSelected: (SettingsDestination) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            DestinationList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                selectedDestination = null,
                onDestinationSelected = onDestinationSelected
            )
        }
    }
}

/* --------------------------------------------------------- */
/* Two Pane Layout (无需修改)                               */
/* --------------------------------------------------------- */

@Composable
private fun TwoPaneLayout(searchState: SettingsSearchState) {
    var selectedDestination by remember { mutableStateOf(SettingsDestination.Basic) }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .fillMaxHeight()
        ) {
            DestinationList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                selectedDestination = selectedDestination,
                onDestinationSelected = { destination ->
                    selectedDestination = destination
                }
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            val targetScreen = selectedDestination.screenFactory()
            Navigator(targetScreen) { navigator ->
                LaunchedEffect(targetScreen) {
                    val current = navigator.lastItem
                    if (current != targetScreen) {
                        navigator.replaceAll(targetScreen)
                    }
                }
                CompositionLocalProvider(LocalSettingsDetailMode provides SettingsDetailMode.TWO_PANE) {
                    CurrentScreen()
                }
            }
        }
    }
}

@Composable
private fun DestinationList(
    modifier: Modifier,
    selectedDestination: SettingsDestination?,
    onDestinationSelected: (SettingsDestination) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SettingsDestination.categories().forEach { destination ->
            DestinationRow(
                destination = destination,
                selected = selectedDestination == destination,
                onClick = { onDestinationSelected(destination) }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        SettingsDestination.additional().forEach { destination ->
            DestinationRow(
                destination = destination,
                selected = selectedDestination == destination,
                onClick = { onDestinationSelected(destination) }
            )
        }
    }
}

@Composable
private fun DestinationRow(
    destination: SettingsDestination,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(destination.titleRes),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(destination.summaryRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
