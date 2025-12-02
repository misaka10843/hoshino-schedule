package com.misaka.hoshinoschedule.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
private fun SinglePaneLayout(
    searchState: SettingsSearchState,
    onDestinationSelected: (SettingsDestination) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsSearchField(
            query = searchState.query,
            onQueryChange = searchState::updateQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (searchState.query.isBlank()) {
            DestinationList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                selectedDestination = null,
                onDestinationSelected = onDestinationSelected
            )
        } else {
            SearchResultsList(
                results = searchState.results,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                onResultClick = { destination ->
                    onDestinationSelected(destination)
                    searchState.clear()
                }
            )
        }
    }
}

@Composable
private fun TwoPaneLayout(searchState: SettingsSearchState) {
    var selectedDestination by remember { mutableStateOf(SettingsDestination.Basic) }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .fillMaxHeight()
        ) {
            SettingsSearchField(
                query = searchState.query,
                onQueryChange = searchState::updateQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            AnimatedVisibility(visible = searchState.query.isNotBlank()) {
                SearchResultsList(
                    results = searchState.results,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    onResultClick = { destination ->
                        selectedDestination = destination
                        searchState.clear()
                    }
                )
            }

            AnimatedVisibility(visible = searchState.query.isBlank()) {
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
private fun SettingsSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.settings_search_hint)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.settings_search_clear))
                }
            }
        },
        singleLine = true
    )
}

@Composable
private fun DestinationList(
    modifier: Modifier,
    selectedDestination: SettingsDestination?,
    onDestinationSelected: (SettingsDestination) -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
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
    val modifier = Modifier
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.large)
        .clickable(onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 14.dp)

    Row(
        modifier = modifier,
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

@Composable
private fun SearchResultsList(
    results: List<SettingsSearchItem>,
    contentPadding: PaddingValues,
    onResultClick: (SettingsDestination) -> Unit
) {
    if (results.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.settings_search_no_results),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results, key = { it.id }) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onResultClick(item.destination) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.destination.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
