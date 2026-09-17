package com.letsfootball.app.ui.screens.fixtures

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.letsfootball.app.data.model.FixtureUi
import com.letsfootball.app.data.repository.League
import com.letsfootball.app.ui.components.DateSectionHeader
import com.letsfootball.app.ui.components.EmptyState
import com.letsfootball.app.ui.components.ErrorState
import com.letsfootball.app.ui.components.FullScreenLoading
import com.letsfootball.app.ui.components.MatchCard
import com.letsfootball.app.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixturesScreen(viewModel: FixturesViewModel) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Let's Football",
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.SportsSoccer,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LeagueTabs(
                selected = state.selectedLeague,
                onSelect = viewModel::onLeagueSelected
            )

            val currentFixturesState = state.fixturesByLeague[state.selectedLeague] ?: UiState.Loading

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = viewModel::onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                when (currentFixturesState) {
                    is UiState.Loading -> FullScreenLoading()
                    is UiState.Error -> ErrorState(
                        message = currentFixturesState.message,
                        onRetry = viewModel::onRefresh
                    )
                    is UiState.Success -> {
                        val fixtures = currentFixturesState.data
                        if (fixtures.isEmpty()) {
                            EmptyState()
                        } else {
                            FixturesList(fixtures = fixtures)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeagueTabs(selected: League, onSelect: (League) -> Unit) {
    val leagues = League.entries
    val selectedIndex = leagues.indexOf(selected).coerceAtLeast(0)

    TabRow(selectedTabIndex = selectedIndex) {
        leagues.forEach { league ->
            Tab(
                selected = league == selected,
                onClick = { onSelect(league) },
                text = {
                    Text(
                        text = league.displayName,
                        fontWeight = if (league == selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
private fun FixturesList(fixtures: List<FixtureUi>) {
    val grouped = fixtures.groupBy { it.date }.toSortedMap()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, bottom = 24.dp
        )
    ) {
        grouped.forEach { (date, matchesForDate) ->
            item(key = "header-$date") {
                DateSectionHeader(rawDate = date)
            }
            items(matchesForDate, key = { it.id }) { fixture ->
                MatchCard(
                    fixture = fixture,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}
