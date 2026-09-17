package com.letsfootball.app.ui.screens.fixtures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsfootball.app.data.model.FixtureUi
import com.letsfootball.app.data.repository.FootballRepository
import com.letsfootball.app.data.repository.League
import com.letsfootball.app.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

data class FixturesScreenState(
    val selectedLeague: League = League.PREMIER_LEAGUE,
    val fixturesByLeague: Map<League, UiState<List<FixtureUi>>> = emptyMap(),
    val isRefreshing: Boolean = false
)

class FixturesViewModel : ViewModel() {

    // Plain no-arg constructor so the default `by viewModels()` factory (which
    // instantiates via reflection) can create this ViewModel without extra wiring.
    private val repository = FootballRepository()

    private val _state = MutableStateFlow(FixturesScreenState())
    val state: StateFlow<FixturesScreenState> = _state.asStateFlow()

    init {
        loadLeague(League.PREMIER_LEAGUE)
    }

    fun onLeagueSelected(league: League) {
        _state.value = _state.value.copy(selectedLeague = league)
        if (_state.value.fixturesByLeague[league] !is UiState.Success) {
            loadLeague(league)
        }
    }

    fun onRefresh() {
        loadLeague(_state.value.selectedLeague, isManualRefresh = true)
    }

    private fun loadLeague(league: League, isManualRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = isManualRefresh,
                fixturesByLeague = _state.value.fixturesByLeague +
                    (league to (if (isManualRefresh) _state.value.fixturesByLeague[league] ?: UiState.Loading else UiState.Loading))
            )

            val result = runCatching { repository.getFixtures(league) }
                .fold(
                    onSuccess = { UiState.Success(it) },
                    onFailure = { throwable ->
                        val message = if (throwable is IOException) {
                            "No internet connection. Pull down to try again."
                        } else {
                            "Couldn't load fixtures. Pull down to try again."
                        }
                        UiState.Error(message)
                    }
                )

            _state.value = _state.value.copy(
                isRefreshing = false,
                fixturesByLeague = _state.value.fixturesByLeague + (league to result)
            )
        }
    }
}
