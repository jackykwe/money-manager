package com.kaeonx.moneymanager.customclasses

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toFormattedString(pattern: String): String {
    val dateFormat = DateFormat.getDateTimeInstance() as SimpleDateFormat
    dateFormat.applyPattern(pattern)
    return dateFormat.format(time)
}

fun Calendar.constructStartOfMonthCalendar(): Calendar {
    set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
    set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
    set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
    set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
    set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
    return this
}

fun Calendar.constructEndOfMonthCalendar(): Calendar {
    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
    set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
    set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
    set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
    return this
}