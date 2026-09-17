package com.letsfootball.app.data.remote

import com.letsfootball.app.data.model.FixturesResponse
import com.letsfootball.app.data.model.TeamLookupResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * TheSportsDB (https://www.thesportsdb.com/api.php) free API.
 * "3" is TheSportsDB's public test API key, free for personal / hobby projects.
 * Swap it for a paid Patreon key in RetrofitClient if you outgrow the free tier.
 */
interface SportsDbApiService {

    @GET("eventsnextleague.php")
    suspend fun getUpcomingFixtures(@Query("id") leagueId: String): FixturesResponse

    @GET("eventspastleague.php")
    suspend fun getPastFixtures(@Query("id") leagueId: String): FixturesResponse

    @GET("lookupteam.php")
    suspend fun getTeam(@Query("id") teamId: String): TeamLookupResponse
}
