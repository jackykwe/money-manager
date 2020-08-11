package com.kaeonx.moneymanager.handlers

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class CalendarHandler private constructor() {

    companion object {

        /**
         * You **must** clone the calendar.
         */
        internal fun getFormattedString(calendar: Calendar, pattern: String): String {
            val dateFormat = DateFormat.getDateTimeInstance() as SimpleDateFormat
            dateFormat.applyPattern(pattern)
            return dateFormat.format(calendar.time)
        }

        internal fun getFormattedString(timestamp: Long, pattern: String): String {
            // In current timezone.
            val c = Calendar.getInstance()
            c.timeInMillis = timestamp
            return getFormattedString(c, pattern)
        }

        /**
         * There is no need to clone either of the calendars, since no modifications will be made
         * to them.
         */
        internal fun calculateNumberOfDays(calendarStart: Calendar, calendarEnd: Calendar): Int =
            BigDecimal(calendarEnd.timeInMillis - calendarStart.timeInMillis)
                .divide(BigDecimal("86400000"), 0, RoundingMode.UP)
                .toInt()

        /**
         * There is no need to clone either of the calendars, since no modifications will be made
         * to them.
         */
        internal fun calculateNumberOfMonths(calendarStart: Calendar, calendarEnd: Calendar): Int {
            val yearDiff = calendarEnd.get(Calendar.YEAR) - calendarStart.get(Calendar.YEAR)
            val monthDiff = calendarEnd.get(Calendar.MONTH) - calendarStart.get(Calendar.MONTH)
            return 1 + yearDiff * 12 + monthDiff
        }

        /**
         * There is no need to clone for `calendarStart`, since no modifications will be made to it.
         *
         * **Note**: The day of `calendarStart` counts as Day 1.
         */
        internal fun calculateDayNumberWRTStartCalendar(
            millis: Long,
            calendarStart: Calendar
        ): Int =
            BigDecimal(millis - calendarStart.timeInMillis)
                .divide(BigDecimal("86400000"), 4, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.FLOOR)
                .toInt()
                .plus(1)  // Floor and plus(1) to account for 00:00 transactions.

        /**
         * You **must** clone the calendar.
         */
        internal fun getStartOfMonthCalendar(calendar: Calendar): Calendar {
            return calendar.apply {
                set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
                set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
                set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
            }
        }

        /**
         * You **must** clone the calendar.
         */
        internal fun getStartOfMonthMillis(calendar: Calendar): Long {
            return calendar.apply {
                set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
                set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
                set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
            }.timeInMillis
        }

        /**
         * You **must** clone the calendar.
         */
        internal fun getEndOfMonthMillis(calendar: Calendar): Long {
            return calendar.apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
                set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
                set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
            }.timeInMillis
        }

        /**
         * You **must** clone the calendar.
         */
        internal fun getEndOfMonthCalendar(calendar: Calendar): Calendar {
            return calendar.apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
                set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
                set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
            }
        }

        internal fun getStartOfYearCalendar(calendar: Calendar): Calendar {
            return calendar.apply {
                set(Calendar.MONTH, getActualMinimum(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
                set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
                set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
            }
        }

        internal fun getEndOfYearCalendar(calendar: Calendar): Calendar {
            return calendar.apply {
                set(Calendar.MONTH, getActualMaximum(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
                set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
                set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
            }
        }

        /**
         * @param displayMonthStartCalendar This Calendar's millis should be equal to the first
         * millisecond of the month. There is no need to clone it.
         * @param scale - Default 2. Increase this to increase accuracy of calculations.
         */
        internal fun getDayDivDays(
            displayMonthStartCalendar: Calendar,
            scale: Int = 2
        ): BigDecimal {
            val currentCal = Calendar.getInstance()
            val currentMonthStartMillis = getStartOfMonthMillis(currentCal.clone() as Calendar)
            val displayMonthStartMillis = displayMonthStartCalendar.timeInMillis
            return when {
                displayMonthStartMillis == currentMonthStartMillis -> {
                    BigDecimal(
                        currentCal.get(Calendar.DAY_OF_MONTH)
                    ).divide(
                        displayMonthStartCalendar.getActualMaximum(
                            Calendar.DAY_OF_MONTH
                        ).toBigDecimal(),
                        scale,
                        RoundingMode.HALF_UP
                    )
                }
                displayMonthStartMillis > currentMonthStartMillis -> BigDecimal.ZERO
                displayMonthStartMillis < currentMonthStartMillis -> BigDecimal.ONE
                else -> throw IllegalStateException("Did I fail math?")
            }
        }

        // Use on timestamps
        internal fun getCalendar(timestamp: Long): Calendar {
            val c = Calendar.getInstance()
            c.timeInMillis = timestamp
            return c
        }
    }
}
