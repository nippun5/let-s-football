package com.letsfootball.app.data.repository

import com.letsfootball.app.data.model.FixtureDto
import com.letsfootball.app.data.model.FixtureUi
import com.letsfootball.app.data.remote.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Competitions available in the app, identified by TheSportsDB league id.
 *  - 4328 = English Premier League
 *  - 4480 = UEFA Champions League
 */
enum class League(val id: String, val displayName: String) {
    PREMIER_LEAGUE(id = "4328", displayName = "Premier League"),
    CHAMPIONS_LEAGUE(id = "4480", displayName = "Champions League")
}

class FootballRepository {

    private val api = RetrofitClient.api

    // In-memory cache of teamId -> real crest URL, since the same clubs repeat across many
    // fixtures. Avoids re-hitting the free API's rate limit for badges we already resolved.
    private val badgeCache = HashMap<String, String?>()
    private val badgeCacheMutex = Mutex()

    /**
     * Combines "next" (upcoming/scheduled) and "past" (recently played) fixtures for a
     * league into a single, time-sorted list so the user sees recent results and the
     * upcoming run of fixtures together, closest-in-time first. Real club crests are
     * resolved per team (and cached) via TheSportsDB's team lookup endpoint.
     */
    suspend fun getFixtures(league: League): List<FixtureUi> = coroutineScope {
        val upcoming = runCatching { api.getUpcomingFixtures(league.id).events }.getOrNull().orEmpty()
        val past = runCatching { api.getPastFixtures(league.id).events }.getOrNull().orEmpty()

        val rawFixtures = (past + upcoming).filterNotNull()

        val teamIds = rawFixtures
            .flatMap { listOfNotNull(it.idHomeTeam, it.idAwayTeam) }
            .distinct()

        // Resolve every distinct team's crest concurrently, then reuse the results below.
        teamIds.map { teamId -> async { teamId to resolveBadge(teamId) } }.awaitAll()

        rawFixtures
            .mapNotNull { it.toFixtureUi(league.displayName) }
            .distinctBy { it.id }
            .sortedBy { it.sortKey }
    }

    private suspend fun resolveBadge(teamId: String): String? {
        badgeCacheMutex.withLock {
            if (badgeCache.containsKey(teamId)) return badgeCache[teamId]
        }
        val badge = runCatching { api.getTeam(teamId).teams?.firstOrNull()?.strTeamBadge }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }

        badgeCacheMutex.withLock { badgeCache[teamId] = badge }
        return badge
    }

    private fun FixtureDto.toFixtureUi(leagueName: String): FixtureUi? {
        val home = strHomeTeam ?: return null
        val away = strAwayTeam ?: return null
        val date = dateEvent ?: return null

        return FixtureUi(
            id = idEvent ?: "$home-$away-$date",
            homeTeam = home,
            awayTeam = away,
            homeBadgeUrl = idHomeTeam?.let { badgeCache[it] },
            awayBadgeUrl = idAwayTeam?.let { badgeCache[it] },
            homeScore = intHomeScore,
            awayScore = intAwayScore,
            date = date,
            time = strTime?.take(5),
            venue = strVenue,
            league = strLeague ?: leagueName,
            isFinished = strStatus.equals("Match Finished", ignoreCase = true) ||
                (!intHomeScore.isNullOrBlank() && !intAwayScore.isNullOrBlank() && strStatus.isNullOrBlank())
        )
    }
}
