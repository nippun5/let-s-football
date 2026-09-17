package com.letsfootball.app.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {

    private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /** "2026-09-12" -> "Saturday, 12 September" (falls back to the raw string if unparsable). */
    fun formatHeaderDate(rawDate: String): String {
        val date = runCatching { LocalDate.parse(rawDate, isoFormatter) }.getOrNull() ?: return rawDate
        val today = LocalDate.now()
        val dayLabel = when (date) {
            today -> "Today"
            today.plusDays(1) -> "Tomorrow"
            today.minusDays(1) -> "Yesterday"
            else -> date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        }
        val monthDay = date.format(DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault()))
        return "$dayLabel, $monthDay"
    }

    fun isPast(rawDate: String): Boolean {
        val date = runCatching { LocalDate.parse(rawDate, isoFormatter) }.getOrNull() ?: return false
        return date.isBefore(LocalDate.now())
    }
}
