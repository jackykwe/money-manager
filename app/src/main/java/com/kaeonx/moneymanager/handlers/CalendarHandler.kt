package com.kaeonx.moneymanager.handlers

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CalendarHandler private constructor() {
    companion object {

        fun getFormattedString(calendar: Calendar, pattern: String): String {
            val dateFormat = DateFormat.getDateTimeInstance() as SimpleDateFormat
            dateFormat.applyPattern(pattern)
            return dateFormat.format(calendar.time)
        }

        fun getFormattedString(timestamp: Long, pattern: String): String {
            // In current timezone.
            val c = Calendar.getInstance()
            c.timeInMillis = timestamp
            return getFormattedString(c, pattern)
        }


        fun getStartOfMonthCalendar(calendar: Calendar): Calendar {
            return calendar.apply {
                set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
                set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
                set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
            }
        }

        fun getStartOfMonthMillis(calendar: Calendar): Long {
            return calendar.apply {
                set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
                set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
                set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
            }.timeInMillis
        }

        fun getEndOfMonthMillis(calendar: Calendar): Long {
            return calendar.apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
                set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
                set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
            }.timeInMillis
        }

        // Use on timestamps
        fun getCalendar(timestamp: Long): Calendar {
            val c = Calendar.getInstance()
            c.timeInMillis = timestamp
            return c
        }
    }
}
