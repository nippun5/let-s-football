package com.letsfootball.app.data.model

import com.google.gson.annotations.SerializedName

/** Raw response wrapper from eventsnextleague.php / eventspastleague.php */
data class FixturesResponse(
    @SerializedName("events") val events: List<FixtureDto>?
)

data class FixtureDto(
    @SerializedName("idEvent") val idEvent: String?,
    @SerializedName("strEvent") val strEvent: String?,
    @SerializedName("strHomeTeam") val strHomeTeam: String?,
    @SerializedName("strAwayTeam") val strAwayTeam: String?,
    @SerializedName("idHomeTeam") val idHomeTeam: String?,
    @SerializedName("idAwayTeam") val idAwayTeam: String?,
    @SerializedName("intHomeScore") val intHomeScore: String?,
    @SerializedName("intAwayScore") val intAwayScore: String?,
    @SerializedName("dateEvent") val dateEvent: String?,
    @SerializedName("strTime") val strTime: String?,
    @SerializedName("strVenue") val strVenue: String?,
    @SerializedName("strLeague") val strLeague: String?,
    @SerializedName("strStatus") val strStatus: String?,
    @SerializedName("strSeason") val strSeason: String?,
    @SerializedName("strThumb") val strThumb: String?
)

/** Raw response wrapper from lookupteam.php */
data class TeamLookupResponse(
    @SerializedName("teams") val teams: List<TeamDto>?
)

data class TeamDto(
    @SerializedName("idTeam") val idTeam: String?,
    @SerializedName("strTeam") val strTeam: String?,
    @SerializedName("strTeamBadge") val strTeamBadge: String?
)

/** Clean model consumed by the UI layer */
data class FixtureUi(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeBadgeUrl: String?,
    val awayBadgeUrl: String?,
    val homeScore: String?,
    val awayScore: String?,
    val date: String,
    val time: String?,
    val venue: String?,
    val league: String,
    val isFinished: Boolean
) {
    val hasScore: Boolean get() = !homeScore.isNullOrBlank() && !awayScore.isNullOrBlank()

    /** Sort/group key, e.g. "2026-09-09" */
    val sortKey: String get() = date + (time ?: "")
}
